int is_folder(const char *path) {
    struct stat path_stat;

    // Get file/directory attributes
    if (stat(path, &path_stat) != 0) {
        perror("Error accessing path");
        return 0; // Error occurred
    }

    // Check if it is a directory
    return S_ISDIR(path_stat.st_mode);
}
int has_job_extension(const char *filename) {
    // Get the length of the filename
    size_t len = strlen(filename);

    // Check if it ends with ".job"
    return len > 4 && strcmp(filename + len - 4, ".job") == 0;
}

void wait_for_all_children() {
    int status;
    pid_t pid;

    // Loop to wait for all child processes
    while ((pid = waitpid(-1, &status, 0)) > 0) {
        // No need to print anything
    }

    // Check for errors other than no more child processes
    if (errno != ECHILD) {
        perror("waitpid");
    }
}