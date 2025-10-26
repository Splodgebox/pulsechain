package net.pulsechain.scheduler;

import net.pulsechain.task.Task;
import net.pulsechain.task.exception.TaskException;

import java.util.List;
import java.util.UUID;

public interface ITaskScheduler {

    void register(Task task);
    void registerAll(List<Task> tasks);
    void executeAll() throws TaskException;
    void execute(Task task) throws TaskException;
    void execute(UUID id) throws TaskException;
    boolean hasCircularDependency();
    void clear();

}
