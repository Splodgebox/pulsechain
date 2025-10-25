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

    public Task(@NonNull String name) {
        this(name, new ArrayList<>());
    }

    public Task(@NonNull String name, List<UUID> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    @Override
    public void addDependency(UUID dependency) throws TaskException {
        if (dependency != null && !dependency.equals(this.id)) {
            dependencies.add(dependency);
        } else {
            throw new TaskException(
                    dependency,
                    TaskException.ErrorType.SELF_DEPENDENCY,
                    "Dependency cannot be the same as the task itself"
            );
        }
    }

    public abstract void execute();

    public List<UUID> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
}