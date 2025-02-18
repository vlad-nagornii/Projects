#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <dirent.h>
#include <string.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <errno.h>
#include <sys/types.h>
#include <pthread.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/types.h>
#include <semaphore.h>
#include <signal.h>

#include "utils.c"
#include "constants.h"
#include "parser.h"
#include "operations.h"
#include "src/common/constants.h"
#include "client.h"
#include "src/common/io.h"

#define MAX_CLIENTS 2

typedef struct {
    int client_id;
    char req_pipe_path[41];
    char resp_pipe_path[41];
    char notif_pipe_path[41];
} client_args;

void end_connections();

void execute_comands(int fd_in, int fd_out, char* out_path);
void execute_requests(int req_fd, int resp_fd, int client_id);

void* connect_clients();
void* file_thread(void* args);
void* client_thread();

char* FOLDER_PATH;
int MAX_BACKUPS;
int MAX_THREADS;
char* SERVER_PIPE_PATH;
HashTable *kvs_table = NULL;
int client_info[MAX_CLIENTS][4];

client_args* client_buffer[MAX_CLIENTS];
int client_count = 0;

int HOST_FD;

sem_t clients_available;
sem_t clients_taken;
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;

pthread_rwlock_t kvs_lock = PTHREAD_RWLOCK_INITIALIZER;
pthread_mutex_t dir_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t backup_mutex = PTHREAD_MUTEX_INITIALIZER;



int main(int argc, char* argv[]) {
    

    if (argc != 5) {
        fprintf(stderr, "Invalid Arugments!\n");
        return 1;
    }

    if (!is_folder(argv[1])) {
      fprintf(stderr, "Invalid Path!\n");
      return 1;
    }
    // TODO: Check if the second and third argument is an integer

    FOLDER_PATH = argv[1];
    MAX_BACKUPS = atoi(argv[2]);
    MAX_THREADS = atoi(argv[3]);
    SERVER_PIPE_PATH = argv[4];

    pthread_t host = 0;
    pthread_t jobs_tid[MAX_THREADS];
    pthread_t clients_tid[MAX_CLIENTS];

    // 2d array com o id e os paths dos fifos de notificacao de cada cliente, inicializada a 0
    memset(client_info, 0, sizeof(client_info));

    // CRIAR buffer que vai ser passado para o thread, onde se vai guardar o path das fifos
    for(int i = 0; i < MAX_CLIENTS; i++){
        client_buffer[i] = malloc(sizeof(client_args));
    }

    sem_init(&clients_available, 0, MAX_CLIENTS);
    sem_init(&clients_taken, 0, 0);

    sigset_t set;

    sigemptyset(&set);
    sigaddset(&set, SIGUSR1);
    pthread_sigmask(SIG_BLOCK, &set, NULL);

    DIR *dir;

    if ((dir = opendir(FOLDER_PATH)) == NULL) {
        perror("opendir");
        return 1;
    }
    kvs_table = kvs_init();
    if (kvs_table == NULL) {
        fprintf(stderr, "Failed to initialize KVS\n");
        return 1;
    }

    // JOBS
    for (int i = 0; i < MAX_THREADS; i++) {
        jobs_tid[i] = 0; // Initialize thread IDs to zero
    }

    for(int i = 0; i < MAX_THREADS; i++){
        if (pthread_create(&jobs_tid[i], NULL, file_thread, dir) != 0) {
            perror("Failed to create thread");
            return 1;
        }
    }
    // CLIENTS
    
    if (pthread_create(&host, NULL, connect_clients, NULL) != 0) {
        perror("Failed to create thread");
        return 1;
    }

    for(int i = 0; i < MAX_CLIENTS; i++){
        if (pthread_create(&clients_tid[i], NULL, client_thread, NULL) != 0) {
            perror("Failed to create thread");
            return 1;
        }
    }

    for(int i = 0; i < MAX_THREADS; i++){
        pthread_join(jobs_tid[i], NULL);
    }
    for(int i = 0; i < MAX_CLIENTS; i++){
        pthread_join(clients_tid[i], NULL);
    }
    pthread_join(host, NULL);


    for(int i = 0; i < MAX_CLIENTS; i++){
        free(client_buffer[i]);
    }

    pthread_mutex_destroy(&client_mutex);
    sem_destroy(&clients_available);    
    sem_destroy(&clients_taken);
    pthread_rwlock_destroy(&kvs_lock);
    pthread_mutex_destroy(&dir_mutex);
    pthread_mutex_destroy(&backup_mutex);
    kvs_terminate();
    closedir(dir); 

    wait_for_all_children();
}   

void* connect_clients(){
    sigset_t set;
    sigemptyset(&set);
    sigaddset(&set, SIGUSR1);
    pthread_sigmask(SIG_UNBLOCK, &set, NULL);

    signal(SIGUSR1, end_connections);

    if (mkfifo(SERVER_PIPE_PATH, 0640) == -1) {
        if(errno != EEXIST) {
            perror("mkfifo");
            return NULL;
        }
    }

    HOST_FD = open(SERVER_PIPE_PATH, O_RDWR);

    if (HOST_FD == -1) {
        printf("Failed to open server pipe\n");
        perror("open");
        return NULL;
    }
    char message[4*41];

    while (1){  
        read_all(HOST_FD, message, 4*41, NULL);

        sem_wait(&clients_available);
        pthread_mutex_lock(&client_mutex); 
        
        client_args* client = malloc(sizeof(client_args));
        char client_id_string[41];
        
        memcpy(client_id_string, message, 41);
        memcpy(client->req_pipe_path, message + 41, 41);
        memcpy(client->resp_pipe_path, message + 2 * 41, 41);
        memcpy(client->notif_pipe_path, message + 3 * 41, 41);

        puts(client_id_string);

        client->client_id = atoi(client_id_string);

        free(client_buffer[client_count]);
        client_buffer[client_count] = client;

        client_count++;

        pthread_mutex_unlock(&client_mutex);
        sem_post(&clients_taken);

    }

    close(HOST_FD);

    unlink(SERVER_PIPE_PATH);
    return NULL;
}

void* file_thread(void* args) {
    DIR * dir = (DIR *)args;
    struct dirent *entry;
    
    while(1){ // iterate over the files in the directory   
        // lock
        pthread_mutex_lock(&dir_mutex);
        entry = readdir(dir);
        pthread_mutex_unlock(&dir_mutex);
        if (entry == NULL) {
            break;
        }
        if (has_job_extension(entry->d_name)) {
            char *dot = strrchr(entry->d_name, '.');
            if (dot != NULL) {
                *dot = '\0'; // Remove a extensão .job
            }
            //unlock
            char in_path[1024];
            snprintf(in_path, sizeof(in_path), "%s/%s.job", FOLDER_PATH, entry->d_name);
    
            char out_path[1024];
            snprintf(out_path, sizeof(out_path), "%s/%s.out", FOLDER_PATH, entry->d_name);
            
            int fd_in = open(in_path, O_RDONLY);
            int fd_out = open(out_path, O_WRONLY | O_CREAT | O_TRUNC | O_SYNC, 0644);


            execute_comands(fd_in, fd_out, out_path);
  
            close(fd_in);
            close(fd_out);
        }

    } 
    return NULL;
}

void* client_thread() {
    while(1){
        sem_wait(&clients_taken);
        int client_id;
        char req_pipe_path[41];
        char resp_pipe_path[41];
        char notif_pipe_path[41];

        pthread_mutex_lock(&client_mutex);

        client_id = client_buffer[client_count - 1]->client_id;
        strcpy(req_pipe_path, client_buffer[client_count - 1]->req_pipe_path);
        strcpy(resp_pipe_path, client_buffer[client_count - 1]->resp_pipe_path);
        strcpy(notif_pipe_path, client_buffer[client_count - 1]->notif_pipe_path);
        client_count--;
   
        pthread_mutex_unlock(&client_mutex);

        int req_fd, resp_fd, notif_fd;
        if((req_fd = open(req_pipe_path, O_RDONLY)) == -1){
            perror("open");
            return NULL;
        }

        if((resp_fd = open(resp_pipe_path, O_WRONLY)) == -1){
            perror("open");
            close(req_fd);
            return NULL;
        }
        if((notif_fd = open(notif_pipe_path, O_WRONLY)) == -1){
            perror("open");
            close(req_fd);
            close(resp_fd);
            return NULL;
        }
        for(int i = 0; i < MAX_CLIENTS; i++){
            if(client_info[i][0] == 0){
                client_info[i][0] = client_id;
                client_info[i][1] = notif_fd;
                client_info[i][2] = req_fd;
                client_info[i][3] = resp_fd;
                break;
            }
        }
        sem_post(&clients_available);
        execute_requests(req_fd, resp_fd, client_id);
    }

}

void execute_requests(int req_fd, int resp_fd, int client_id){
char response;
    while(1){
        char op_code;
        read(req_fd, &op_code, 1);
        char key[41];
        
        switch(op_code){
            case '2':
                response = disconnect_client(client_id);
                write(resp_fd, &response, 1); // 1 = sucesso
                write(resp_fd, &op_code, 1);
                close(req_fd);
                close(resp_fd);
                return;

            case '3': // subscribe
                read(req_fd, key, 41);
                response = subscribe(client_id, key);
                write(resp_fd, &response, 1); // 1 = sucesso
                write(resp_fd, &op_code, 1); // 3 = op_code
                break;
            case '4': // unsubscribe
                read(req_fd, key, 41);
                response = unsubscribe(client_id, key);
                write(resp_fd, &response, 1); // 1 = sucesso
                write(resp_fd, &op_code, 1); // 4 = op_code
                break;
        }
    }

}
void execute_comands(int fd_in, int fd_out, char* out_path) {
    int backup_instances = 0;
    while (1) {
        char  keys[MAX_WRITE_SIZE][MAX_STRING_SIZE] = {0};
        char values[MAX_WRITE_SIZE][MAX_STRING_SIZE] = {0};
        unsigned int delay;
        size_t num_pairs;

        switch (get_next(fd_in)) {
            case CMD_WRITE:
                num_pairs = parse_write(fd_in, keys, values, MAX_WRITE_SIZE, MAX_STRING_SIZE);
                if (num_pairs == 0) {
                    fprintf(stderr, "Invalid command. See HELP for usage\n");
                    continue;
                }

                if (kvs_write(num_pairs, keys, values)) {
                    fprintf(stderr, "Failed to write pair\n");
                }
                break;

            case CMD_READ:
                num_pairs = parse_read_delete(fd_in, keys, MAX_WRITE_SIZE, MAX_STRING_SIZE);
                if (num_pairs == 0) {
                    fprintf(stderr, "Invalid command. See HELP for usage\n");
                    continue;
                }

                pthread_rwlock_rdlock(&kvs_lock);
                if (kvs_read(num_pairs, keys, fd_out)) {
                    fprintf(stderr, "Failed to read pair\n");
                }
                pthread_rwlock_unlock(&kvs_lock);
                break;

            case CMD_DELETE:
                num_pairs = parse_read_delete(fd_in, keys, MAX_WRITE_SIZE, MAX_STRING_SIZE);
                if (num_pairs == 0) {
                    fprintf(stderr, "Invalid command. See HELP for usage\n");
                    continue;
                }

                if (kvs_delete(num_pairs, keys, fd_out)) {
                    fprintf(stderr, "Failed to delete pair\n");
                }
                break;

            case CMD_SHOW:
                kvs_show(fd_out);
                break;

            case CMD_WAIT:
                if (parse_wait(fd_in, &delay, NULL) == -1) {
                    fprintf(stderr, "Invalid command. See HELP for usage\n");
                    continue;
                }

                if (delay > 0) {
                    kvs_wait(delay);
                }
                break;

            case   CMD_BACKUP:
            {
                pthread_mutex_lock(&backup_mutex);

                if (MAX_BACKUPS < 1){
                    wait(NULL);
                }

                MAX_BACKUPS--;

                pthread_mutex_unlock(&backup_mutex);
                backup_instances++;
                
                pid_t pid = fork();
                if(pid < 0) {
                    perror("Failed to fork");
                    return;
                } 
                if(pid == 0) {
                    kvs_backup(out_path, backup_instances);
                    kvs_terminate();
                    exit(0);
                }
                break;    
            }

            case CMD_INVALID:
                fprintf(stderr, "Invalid command. See HELP for usage\n");
                break;

            case CMD_HELP:
                printf( 
                    "Available commands:\n"
                    "  WRITE [(key,value)(key2,value2),...]\n"
                    "  READ [key,key2,...]\n"
                    "  DELETE [key,key2,...]\n"
                    "  SHOW\n"
                    "  WAIT <delay_ms>\n"
                    "  BACKUP\n"
                    "  HELP\n"
                );
                break;

            case CMD_EMPTY:
                break;

            case EOC:
                return;
        }
    }
}


void end_connections() {
    for(int i = 0; i < TABLE_SIZE; i++){
        pthread_rwlock_wrlock(&kvs_table->kvs_lock[i]);
        KeyNode *keyNode = kvs_table->table[i];
        while (keyNode != NULL) {
            SubscriptionNode *sub = keyNode->subscribers;
            while (sub != NULL) {
                SubscriptionNode *next_sub = sub->next;
                free(sub);
                sub = next_sub;
            }
            keyNode->subscribers = NULL; 
            keyNode = keyNode->next;
        }
        pthread_rwlock_unlock(&kvs_table->kvs_lock[i]);
    }

    pthread_mutex_lock(&client_mutex);
    for(int i = 0; i < MAX_CLIENTS; i++){
        if (client_info[i][0] != 0){
            close(client_info[i][1]);
            close(client_info[i][2]);
            close(client_info[i][3]);
            client_info[i][0] = 0;
            client_info[i][1] = 0;
            client_info[i][2] = 0;
            client_info[i][3] = 0;
        }
    }

    pthread_mutex_unlock(&client_mutex);
    sem_destroy(&clients_available);
    sem_destroy(&clients_taken);
    sem_init(&clients_available, 0, MAX_CLIENTS);
    sem_init(&clients_taken, 0, 0);

    close(HOST_FD);
    unlink(SERVER_PIPE_PATH);
    mkfifo(SERVER_PIPE_PATH, 0640);
    HOST_FD = open(SERVER_PIPE_PATH, O_RDWR);

    client_count = 0;
}
