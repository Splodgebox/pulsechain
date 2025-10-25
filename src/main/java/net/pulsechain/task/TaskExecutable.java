package net.pulsechain.task;

import net.pulsechain.task.exception.TaskException;

import java.util.UUID;

public interface TaskExecutable {
    void execute();
    void addDependency(UUID id) throws TaskException;
}
