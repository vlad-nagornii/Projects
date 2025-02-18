#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <semaphore.h>
#include <unistd.h>
#include <fcntl.h>
#include <pthread.h>

#include "kvs.h"
#include "constants.h" 

extern struct HashTable* kvs_table;

/// Calculates a timespec from a delay in milliseconds.
/// @param delay_ms Delay in milliseconds.
/// @return Timespec with the given delay.
static struct timespec delay_to_timespec(unsigned int delay_ms) {
  return (struct timespec){delay_ms / 1000, (delay_ms % 1000) * 1000000};
}
void kvs_show(int fd_out);

int compare(const void *a, const void *b) {
    const char **str_a = (const char **)a;
    const char **str_b = (const char **)b;
    return strcmp(*str_a, *str_b);
}
struct HashTable* kvs_init() {
  if (kvs_table != NULL) {
    fprintf(stderr, "KVS state has already been initialized\n");
    return NULL;
  }

  kvs_table = create_hash_table();
  return kvs_table;
}

int kvs_terminate() { 
  if (kvs_table == NULL) {
    fprintf(stderr, "KVS state must be initialized\n");
    return 1;
  }

  free_table(kvs_table);
  kvs_table = NULL;
  return 0;
}

int kvs_write(size_t num_pairs, char keys[][MAX_STRING_SIZE], char values[][MAX_STRING_SIZE]) {
  if (kvs_table == NULL) {
    fprintf(stderr, "KVS state must be initialized\n");
    return 1;
  }

  for(size_t k = 0; k < num_pairs; k++) {
    pthread_rwlock_wrlock(&kvs_table->kvs_lock[hash(keys[k])]);
  }
  for (size_t i = 0; i < num_pairs; i++) {
    if (write_pair(kvs_table, keys[i], values[i]) != 0) {
      fprintf(stderr, "Failed to write keypair (%s,%s)\n", keys[i], values[i]);
    }
  }

  for(size_t k = 0; k < num_pairs; k++) {
    pthread_rwlock_unlock(&kvs_table->kvs_lock[hash(keys[k])]);
  }

  return 0;
}

int kvs_read(size_t num_pairs, char keys[][MAX_STRING_SIZE], int fd_out) {
  
  if (kvs_table == NULL) {
    const char *error_msg = "KVS state must be initialized\n";
    write(fd_out, error_msg, strlen(error_msg));
    return 1;
  }

  // Sort the keys and remove duplicates
  char* cpy1[num_pairs];
  char* cpy2[num_pairs + 1];
  for(size_t i = 0; i < num_pairs; i++) {
    cpy1[i] = strdup(keys[i]);
    cpy2[i] = NULL;
  }
  cpy2[num_pairs] = NULL;

  qsort(cpy1, num_pairs, sizeof(char*), compare);
  cpy2[0] = cpy1[0];
  size_t last = 0;
  int j = 1;
  for(size_t i = 1; i < num_pairs; i++) {
    if(strcmp(cpy1[i], cpy1[last]) == 0) {
      continue;
    }else{
      cpy2[j] = cpy1[i];
      last = i;
      j++;
    }
  }
  
  size_t k = 0;
  while(cpy2[k] != NULL) {
    pthread_rwlock_rdlock(&kvs_table->kvs_lock[hash(cpy2[k])]);
    k++;
  }

  write(fd_out, "[", 1);
  for (size_t i = 0; i < num_pairs; i++) {
    char buffer[1024];
    char* result = read_pair(kvs_table, cpy2[i]);
    if (result == NULL) {
      snprintf(buffer, sizeof(buffer), "(%s,KVSERROR)", cpy2[i]);
    } else {
      snprintf(buffer, sizeof(buffer), "(%s,%s)", cpy2[i], result);
    }
    write(fd_out, buffer, strlen(buffer));
    free(result);
  }
  write(fd_out, "]\n", 2);

  k = 0;
  while(cpy2[k] != NULL) {
    pthread_rwlock_unlock(&kvs_table->kvs_lock[hash(cpy2[k])]);
    k++;
  }

  for (size_t i = 0; i < num_pairs; i++) {
        free(cpy1[i]); // Free the duplicated strings
  }
  return 0;
}


int kvs_delete(size_t num_pairs, char keys[][MAX_STRING_SIZE], int fd_out) {
  if (kvs_table == NULL) {
    const char *error_msg = "KVS state must be initialized\n";
    write(fd_out, error_msg, strlen(error_msg));
    return 1;
  }

  // Sort the keys and remove duplicates
  char* cpy1[num_pairs + 1];
  for(size_t i = 0; i < num_pairs; i++) {
    cpy1[i] = strdup(keys[i]);
  }
  cpy1[num_pairs] = NULL;

  qsort(cpy1, num_pairs, sizeof(char*), compare);


  int aux = 0;

  for(size_t i = 0; i < num_pairs; i++) {
    pthread_rwlock_wrlock(&kvs_table->kvs_lock[hash(cpy1[i])]);
  }


  for(size_t i = 0; i < num_pairs; i++) {
    if (delete_pair(kvs_table, cpy1[i]) != 0) {
      if (!aux) {
        write(fd_out, "[", 1);
        aux = 1;
      }
      char buffer[1024];
      snprintf(buffer, sizeof(buffer), "(%s,KVSMISSING)", keys[i]);
      write(fd_out, buffer, strlen(buffer));
    }
  }
  if (aux) {
    write(fd_out, "]\n", 2);
  }

  for(size_t i = 0; i < num_pairs; i++) {
    pthread_rwlock_unlock(&kvs_table->kvs_lock[hash(cpy1[i])]);
  }
  
  return 0;
}

void kvs_show(int fd_out) {
  for(int i = 0; i < TABLE_SIZE; i++) {
    pthread_rwlock_rdlock(&kvs_table->kvs_lock[i]);
  }

  for (int i = 0; i < TABLE_SIZE; i++) {
    KeyNode *keyNode = kvs_table->table[i];
    while (keyNode != NULL) {
      char buffer[1024];
      snprintf(buffer, sizeof(buffer), "(%s, %s)\n", keyNode->key, keyNode->value);
      write(fd_out, buffer, strlen(buffer));
      fsync(fd_out);
      keyNode = keyNode->next; // Move to the next node
    }
  }
  
  for(int i = 0; i < TABLE_SIZE; i++) {
    pthread_rwlock_unlock(&kvs_table->kvs_lock[i]);
  }
}


int kvs_backup(char* out_path, int backup_instances) {
// Processo filho
  char backup_file[1024];

  char *dot = strrchr(out_path, '.');
  if (dot != NULL) {
      *dot = '\0'; // Remove a extensão .out
  }

  snprintf(backup_file, sizeof(backup_file), "%s-%d.bck", out_path, backup_instances);

  int fd_bck = open(backup_file, O_WRONLY | O_CREAT | O_TRUNC, 0644);

  if (fd_bck == -1) {
      perror("Failed to open backup file");
      exit(1); // Encerra o processo filho em caso de erro
  }

  for (int i = 0; i < TABLE_SIZE; i++) {
    KeyNode *keyNode = kvs_table->table[i];
    while (keyNode != NULL) {
      char buffer[1024];
      snprintf(buffer, sizeof(buffer), "(%s, %s)\n", keyNode->key, keyNode->value);
      write(fd_bck, buffer, strlen(buffer));
      keyNode = keyNode->next; // Move to the next node
    }
  }

  close(fd_bck);

  exit(0); // Finaliza o processo filho após o backup
}

void kvs_wait(unsigned int delay_ms) {
  struct timespec delay = delay_to_timespec(delay_ms);
  nanosleep(&delay, NULL);
}