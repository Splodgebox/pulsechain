package net.pulsechain.task.handler;

import net.pulsechain.task.Task;
import net.pulsechain.task.exception.TaskException;
import net.pulsechain.task.handler.result.TaskResult;

public interface ITaskHandler {

    TaskResult executeTask(Task task) throws TaskException;
    boolean retry(Task task, boolean force) throws TaskException;

}
