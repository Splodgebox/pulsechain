package net.pulsechain.task.registry;

import net.pulsechain.task.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface ITaskRegistry {

    void register(Task task);
    void unregister(UUID id);
    Optional<Task> get(UUID id);
    Collection<Task> getAllTasks();
    ConcurrentHashMap<UUID, Task> getTasks();
}
