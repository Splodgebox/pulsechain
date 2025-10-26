package net.pulsechain.task.handler;

import net.pulsechain.task.Task;
import net.pulsechain.task.TaskState;
import net.pulsechain.task.exception.TaskException;
import net.pulsechain.task.handler.result.TaskResult;

public class TaskHandler implements ITaskHandler {

    @Override
    public TaskResult executeTask(Task task) throws TaskException {
        if (task == null) {
            throw new TaskException(TaskException.ErrorType.TASK_NOT_FOUND, "Error retrying task: Task is null");
        }

        try {
            task.setState(TaskState.RUNNING);
            task.execute();
            task.setState(TaskState.SUCCESS);
            return TaskResult.success(task);
        } catch (Exception e) {
            task.setState(TaskState.FAILED);
            return TaskResult.failure(task, e);
        }
    }

    @Override
    public boolean retry(Task task, boolean force) throws TaskException  {
        if (task == null) {
            throw new TaskException(TaskException.ErrorType.TASK_NOT_FOUND, "Error retrying task: Task is null");
        }

        if (force || task.getState() == TaskState.FAILED) {
            TaskResult result = executeTask(task);
            return result.getFinalState() == TaskState.SUCCESS;
        }

        return false;
    }

}
