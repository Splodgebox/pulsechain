package net.pulsechain.task.registry;

import lombok.extern.log4j.Log4j2;
import net.pulsechain.task.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class TaskRegistry implements ITaskRegistry {

    private final ConcurrentHashMap<UUID, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public void register(Task task) {
        tasks.put(task.getId(), task);
        log.debug("Registered task {}", task.getName());
    }

    @Override
    public void unregister(UUID id) {
        tasks.remove(id);
        log.debug("Unregistered task {}", id);
    }

    @Override
    public Optional<Task> get(UUID id) {
        log.debug("Getting task {}", id);
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @Override
    public ConcurrentHashMap<UUID, Task> getTasks() {
        return new ConcurrentHashMap<>(tasks);
    }

}
