#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "api.h"
#include "src/common/constants.h" 
#include "src/common/protocol.h"
#include "src/common/io.h"

extern int HOST_FD;
extern int REQ_FD;
extern int RESP_FD;
extern int NOTIF_FD;
extern char REQ_PIPE_PATH[41];
extern char RESP_PIPE_PATH[41];
extern char NOTIF_PIPE_PATH[41];


int kvs_connect(char const* req_pipe_path, 
                char const* resp_pipe_path,
                char const* notif_pipe_path,
                int client_id) {
  // connect pipes
  char client_id_string[41];
  snprintf(client_id_string, 41, "%d", client_id);

  char message[4*41];
  memcpy(message, client_id_string, 41);
  memcpy(message + 41, req_pipe_path, 41);
  memcpy(message + 2 * 41, resp_pipe_path, 41);
  memcpy(message + 3 * 41, notif_pipe_path, 41);

  if (write_all(HOST_FD, message, 4*41) == -1) {
    perror("write");
    close(HOST_FD);
    return 1;
  }

  if (mkfifo(req_pipe_path, 0640) == -1) {
      if(errno != EEXIST) {
          perror("mkfifo");
          return 1;
      }
  }
  if (mkfifo(resp_pipe_path, 0640) == -1) {
      if(errno != EEXIST) {
          perror("mkfifo");
          unlink(req_pipe_path);
          return 1;
      }
  }
  if (mkfifo(notif_pipe_path, 0640) == -1) {
      if(errno != EEXIST) {
          unlink(req_pipe_path);
          unlink(resp_pipe_path);
          perror("mkfifo");
          return 1;
      }
  }
  if((REQ_FD = open(req_pipe_path, O_WRONLY)) == -1){
    perror("open");
    unlink(req_pipe_path);
    unlink(resp_pipe_path);
    unlink(notif_pipe_path);
    return 1;
  }
  if((RESP_FD = open(resp_pipe_path, O_RDONLY)) == -1){
    perror("open");
    close(REQ_FD);
    unlink(req_pipe_path);
    unlink(resp_pipe_path);
    unlink(notif_pipe_path);
    return 1;
  }
  if((NOTIF_FD = open(notif_pipe_path, O_RDONLY)) == -1){
    perror("open");
    close(REQ_FD);
    close(RESP_FD);
    unlink(req_pipe_path);
    unlink(resp_pipe_path);
    unlink(notif_pipe_path);
    return 1;
  }
  return 0;
}
 
int kvs_disconnect(void) {
  char op_code = '2';
  write(REQ_FD, &op_code, 1);
  
  char server_respose;
  char op_code_response;
  
  read(RESP_FD, &server_respose, 1);
  read(RESP_FD, &op_code_response, 1);
  
  printf("Server returned %c for operation disconnect. Op_code: %c\n", server_respose ,op_code_response);
  
  close(RESP_FD);
  close(REQ_FD);
  close(NOTIF_FD);
  unlink(REQ_PIPE_PATH);
  unlink(RESP_PIPE_PATH);
  unlink(NOTIF_PIPE_PATH);
  return 0;
}

int kvs_subscribe(const char* key) {
  // send subscribe message to request pipe and wait for response in response pipe
  char op_code = '3';
  write(REQ_FD, &op_code, 1);
  write(REQ_FD, key, 41);

  char server_respose;
  char op_code_response;
  read(RESP_FD, &server_respose, 1);
  read(RESP_FD, &op_code_response, 1);

  printf("Server returned %c for operation subscribe. Op_code: %c\n", server_respose ,op_code_response);
  return 0;
}

int kvs_unsubscribe(const char* key) {
    // send unsubscribe message to request pipe and wait for response in response pipe
  char op_code = '4';
  write(REQ_FD, &op_code, 1);
  write(REQ_FD, key, 41);

  char server_respose;
  char op_code_response;
  read(RESP_FD, &server_respose, 1);
  read(RESP_FD, &op_code_response, 1);
  printf("Server returned %c for operation subscribe. Op_code: %c\n", server_respose ,op_code_response);
  
  return 0;
}


