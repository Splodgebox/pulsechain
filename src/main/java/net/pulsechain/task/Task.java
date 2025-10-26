package net.pulsechain.task;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.pulsechain.task.exception.TaskException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
public abstract class Task implements TaskExecutable {

    private final UUID id = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();

    private String name;
    private final List<UUID> dependencies;
    private TaskState state = TaskState.PENDING;

    private int retryCount = 0;
    private int maxRetries = 3;

    public Task(@NonNull String name) {
        this.name = name;
        this.dependencies = new ArrayList<>();
    }

    public Task(@NonNull String name, UUID... dependencies) {
        this.name = name;
        this.dependencies = List.of(dependencies);
    }

    @Override
    public void addDependency(Task task) throws TaskException {
        if (task == null) {
            throw new TaskException(
                    null,
                    TaskException.ErrorType.NULL_DEPENDENCY,
                    "Dependency cannot be null"
            );
        }

        UUID dependencyId = task.getId();

        if (dependencyId.equals(this.id)) {
            throw new TaskException(
                    dependencyId,
                    TaskException.ErrorType.SELF_DEPENDENCY,
                    "Dependency cannot be the same as the task itself"
            );
        }

        dependencies.add(dependencyId);
    }

    @Override
    public void addDependencies(Task... tasks) throws TaskException {
        for (Task task : tasks) {
            addDependency(task);
        }
    }

    @Override
    public Task withMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public abstract void execute();

    public List<UUID> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
}