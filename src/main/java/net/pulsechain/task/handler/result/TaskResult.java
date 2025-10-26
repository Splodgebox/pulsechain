package net.pulsechain.task.handler.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.pulsechain.task.Task;
import net.pulsechain.task.TaskState;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class TaskResult {

    private final Task task;
    private final TaskState finalState;
    private final LocalDateTime executionTime;
    private final int retryCount;

    private final Throwable error;

    public static TaskResult success(Task task) {
        return new TaskResult(task, TaskState.SUCCESS, LocalDateTime.now(), task.getRetryCount(), null);
    }

    public static TaskResult failure(Task task, Throwable error) {
        return new TaskResult(task, TaskState.FAILED, LocalDateTime.now(), task.getRetryCount(), error);
    }

    public static TaskResult blocked(Task task) {
        return new TaskResult(task, TaskState.BLOCKED, LocalDateTime.now(), task.getRetryCount(), null);
    }
}
