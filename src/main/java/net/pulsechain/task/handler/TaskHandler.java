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

        while (task.getRetryCount() < task.getMaxRetries()) {
            try {
                task.setState(TaskState.RUNNING);
                task.execute();
                task.setState(TaskState.SUCCESS);
                return TaskResult.success(task);
            } catch (Exception e) {
                task.setRetryCount(task.getRetryCount() + 1);
                if (task.getRetryCount() >= task.getMaxRetries()) {
                    task.setState(TaskState.FAILED);
                    return TaskResult.failure(task, e);
                }
            }
        }

        return TaskResult.failure(task, new Exception("Unknown retry logic failure"));
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
