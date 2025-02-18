// filepath: /Users/vladnagornii/Desktop/SO/projeto-so-2/src/server/client.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include "client.h"
#include "kvs.h"
#include "src/common/io.h"

#define MAX_CLIENTS 10

extern int client_info[MAX_CLIENTS][2];
extern struct HashTable *kvs_table;
extern pthread_mutex_t client_mutex;

char subscribe(int client_id, char key[41]){
    int index = hash(key);
    pthread_rwlock_wrlock(&kvs_table->kvs_lock[index]);
    KeyNode *keyNode = kvs_table->table[index];

    while (keyNode != NULL) {
        if (strcmp(keyNode->key, key) == 0) {

            SubscriptionNode *sub = keyNode->subscribers;
            SubscriptionNode *prev = NULL;
            while(sub != NULL){
                if (sub->client_id == client_id){
                    pthread_rwlock_unlock(&kvs_table->kvs_lock[index]);
                    return '0';
                }
                prev = sub;
                sub = sub->next;
            }
            SubscriptionNode *new_sub = malloc(sizeof(SubscriptionNode));
            new_sub->client_id = client_id;
            new_sub->next = NULL;
            if(prev == NULL){
                keyNode->subscribers = new_sub;
            }else{
                prev->next = new_sub;
            }
            pthread_rwlock_unlock(&kvs_table->kvs_lock[index]);
            return '0';
        }
        keyNode = keyNode->next; // Move to the next node 
    }
    pthread_rwlock_unlock(&kvs_table->kvs_lock[index]);
    return '1';
}


char unsubscribe(int client_id, char key[41]){
    int index = hash(key);
    pthread_rwlock_wrlock(&kvs_table->kvs_lock[index]);
    KeyNode *keyNode = kvs_table->table[index];

    while (keyNode != NULL) {
        if (strcmp(keyNode->key, key) == 0) {
            SubscriptionNode *sub = keyNode->subscribers;
            SubscriptionNode *prev = NULL;
            while(sub != NULL){
                if (sub->client_id == client_id){
                    if(prev == NULL){
                        keyNode->subscribers = sub->next;
                    }else{
                        prev->next = sub->next;
                    }
                    free(sub);
                    pthread_rwlock_unlock(&kvs_table->kvs_lock[index]);
                    return '0';
                }
                prev = sub;
                sub = sub->next;
            }
            break;
        }
        keyNode = keyNode->next; // Move to the next node 
    }
    pthread_rwlock_unlock(&kvs_table->kvs_lock[index]);
    return '1';
}

char disconnect_client(int client_id){
    //remove client from client_info
    pthread_mutex_lock(&client_mutex);
    for(int i = 0; i < MAX_CLIENTS; i++){
        if (client_info[i][0] == client_id){
            close(client_info[i][1]);
            client_info[i][0] = 0;
            client_info[i][1] = 0;
        }
    }
    pthread_mutex_unlock(&client_mutex);
    //remove subscriptions
    for(int i = 0; i < TABLE_SIZE; i++){
        pthread_rwlock_wrlock(&kvs_table->kvs_lock[i]);
        KeyNode *keyNode = kvs_table->table[i];
        while (keyNode != NULL) {
            SubscriptionNode *sub = keyNode->subscribers;
            SubscriptionNode *prev = NULL;
            while(sub != NULL){
                if (sub->client_id == client_id){
                    if(prev == NULL){
                        keyNode->subscribers = sub->next;
                    }else{
                        prev->next = sub->next;
                    }
                    free(sub);
                    break;
                }
                prev = sub;
                sub = sub->next;
            }
            keyNode = keyNode->next; // Move to the next node 
        }
        pthread_rwlock_unlock(&kvs_table->kvs_lock[i]);
    }
    return '0';
}


void send_notification(int client_id, const char key[41], const char value[41]){
    // Implementation of send_notification
    for(int i = 0; i < MAX_CLIENTS; i++){
        if (client_info[i][0] == client_id){

            char message[2 * 41];
            memcpy(message, key, 41);
            memcpy(message + 41, value, 41);

            write_all(client_info[i][1], message, 2*41);
            return;
        }
    }
}