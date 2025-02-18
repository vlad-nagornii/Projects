#include <fcntl.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>


#include "parser.h"
#include "src/client/api.h"
#include "src/common/constants.h"
#include "src/common/io.h"



int HOST_FD;
int REQ_FD;
int RESP_FD; 
int NOTIF_FD;

int CLIENT_ID;
char REQ_PIPE_PATH[41];
char RESP_PIPE_PATH[41];
char NOTIF_PIPE_PATH[41];

int SIGNAL;

void* notif_thread();
void execute_commands(char keys[MAX_NUMBER_SUB][MAX_STRING_SIZE]);

int main(int argc, char* argv[]) {
  if (argc < 3) {
    fprintf(stderr, "Usage: %s <client_unique_id> <register_pipe_path>\n", argv[0]);
    return 1;
  }

  CLIENT_ID = atoi(argv[1]);
  char* server_pipe_path = argv[2]; 

  //REQ_PIPE_PATH[41] = "/tmp/req";
  //RESP_PIPE_PATH[41] = "/tmp/resp";
  //NOTIF_PIPE_PATH[41] = "/tmp/notif";
   strncpy(REQ_PIPE_PATH, "/tmp/req", sizeof(REQ_PIPE_PATH) - 1);
    REQ_PIPE_PATH[sizeof(REQ_PIPE_PATH) - 1] = '\0'; // Ensure null-termination

    strncpy(RESP_PIPE_PATH, "/tmp/resp", sizeof(RESP_PIPE_PATH) - 1);
    RESP_PIPE_PATH[sizeof(RESP_PIPE_PATH) - 1] = '\0'; // Ensure null-termination

    strncpy(NOTIF_PIPE_PATH, "/tmp/notif", sizeof(NOTIF_PIPE_PATH) - 1);
    NOTIF_PIPE_PATH[sizeof(NOTIF_PIPE_PATH) - 1] = '\0';

  SIGNAL = 0;

  if(CLIENT_ID == 0) {
    fprintf(stderr, "Invalid client id. Client id cannot be '0'\n");
    return 1;
  }

  strncat(REQ_PIPE_PATH, argv[1], 32);
  strncat(RESP_PIPE_PATH, argv[1], 32);
  strncat(NOTIF_PIPE_PATH, argv[1], 32);
 
  char keys[MAX_NUMBER_SUB][MAX_STRING_SIZE] = {0};

  
  //connect to server
  HOST_FD = open(server_pipe_path, O_WRONLY);
  if (HOST_FD == -1) {
      perror("open");
      return 1;
  }

 if (kvs_connect(REQ_PIPE_PATH, RESP_PIPE_PATH, NOTIF_PIPE_PATH, CLIENT_ID) != 0) {
    fprintf(stderr, "Failed to connect to the server\n");
    return 1;
  }


  pthread_t notif_tid;
  if (pthread_create(&notif_tid, NULL, notif_thread, NULL) != 0) {
        perror("Failed to create thread");
        return 1;
    }
  execute_commands(keys);
  close(HOST_FD);
  pthread_join(notif_tid, NULL);
}



void execute_commands(char keys[MAX_NUMBER_SUB][MAX_STRING_SIZE]) {
  size_t num;
  unsigned int delay_ms;

  while (1) {
    if(SIGNAL == 1){
      return;
    }
    switch (get_next(STDIN_FILENO)) {
      case CMD_DISCONNECT:
        puts("disconnecting");
        if (kvs_disconnect() != 0) {
          fprintf(stderr, "Failed to disconnect to the server\n");
          return;
        }
        puts("disconnected");
        return;

      case CMD_SUBSCRIBE:
        printf("subscribing\n");
        num = parse_list(STDIN_FILENO, keys, 1, MAX_STRING_SIZE);
        if (num == 0) {
          fprintf(stderr, "Invalid command. See HELP for usage\n");
          continue;
        }
         
        if (kvs_subscribe(keys[0])) {
            fprintf(stderr, "Command subscribe failed\n");
        }

        break;

      case CMD_UNSUBSCRIBE:
        num = parse_list(STDIN_FILENO, keys, 1, MAX_STRING_SIZE);
        if (num == 0) {
          fprintf(stderr, "Invalid command. See HELP for usage\n");
          continue;
        }
         
        if (kvs_unsubscribe(keys[0])) {
            fprintf(stderr, "Command subscribe failed\n");
        }

        break;

      case CMD_DELAY:
        if (parse_delay(STDIN_FILENO, &delay_ms) == -1) {
          fprintf(stderr, "Invalid command. See HELP for usage\n");
          continue;
        }

        if (delay_ms > 0) {
            printf("Waiting...\n");
            delay(delay_ms);
        }
        break;

      case CMD_INVALID:
        fprintf(stderr, "Invalid command. See HELP for usage\n");
        break;

      case CMD_EMPTY:
        break;

      case EOC:
        // input should end in a disconnect, or it will loop here forever
        break;
    }
  }
}

void* notif_thread(){
    while(1){ 
        char key[41];
        char value[41];
        char message[2 * 41];
        if(read_all(NOTIF_FD, message, 2*41, NULL) <= 0 ){
          SIGNAL = 1;
          return NULL;
        }
        memcpy(key, message, 41);
        memcpy(value, message + 41, 41); 
        printf("(%s,%s)\n", key, value);
    }
    return NULL;
}
