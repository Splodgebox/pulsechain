package net.pulsechain.task.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskException extends Exception {

    public enum ErrorType {
        SELF_DEPENDENCY("Task cannot depend on itself"),
        NULL_DEPENDENCY("Dependency cannot be null"),
        INVALID_DEPENDENCY("Invalid dependency specified"),
        TASK_NOT_FOUND("Task not found"),
        CIRCULAR_DEPENDENCY("Circular dependency detected");

        private final String defaultMessage;

        ErrorType(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
    }

    private final UUID invalidDependency;
    private final ErrorType errorType;

    public TaskException(UUID invalidDependency, ErrorType errorType) {
        super(errorType.defaultMessage);
        this.invalidDependency = invalidDependency;
        this.errorType = errorType;
    }

    public TaskException(UUID invalidDependency, ErrorType errorType, String customMessage) {
        super(customMessage);
        this.invalidDependency = invalidDependency;
        this.errorType = errorType;
    }

    public TaskException(ErrorType errorType) {
        super(errorType.defaultMessage);
        this.invalidDependency = null;
        this.errorType = errorType;
    }

    public TaskException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.invalidDependency = null;
        this.errorType = errorType;
    }

}

