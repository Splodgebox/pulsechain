package net.pulsechain.task;

import net.pulsechain.task.exception.TaskException;

public interface TaskExecutable {
    void execute();
    void addDependency(Task task) throws TaskException;
    void addDependencies(Task... tasks) throws TaskException;
    Task withMaxRetries(int maxRetries);
}
