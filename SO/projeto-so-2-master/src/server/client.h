// filepath: /Users/vladnagornii/Desktop/SO/projeto-so-2/src/server/client.h
#ifndef CLIENT_H
#define CLIENT_H

#include "kvs.h"

char subscribe(int client_id, char key[41]);
char unsubscribe(int client_id, char key[41]);
char disconnect_client(int client_id);
void send_notification(int client_id, const char key[41], const char value[41]);

#endif // CLIENT_H