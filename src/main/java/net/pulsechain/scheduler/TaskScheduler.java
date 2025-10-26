package net.pulsechain.scheduler;

import net.pulsechain.task.Task;
import net.pulsechain.task.TaskState;
import net.pulsechain.task.exception.TaskException;
import net.pulsechain.task.handler.TaskHandler;
import net.pulsechain.task.registry.TaskRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskScheduler implements ITaskScheduler, AutoCloseable {

    private final ConcurrentLinkedQueue<Task> scheduledTasks = new ConcurrentLinkedQueue<>();

    private final TaskHandler taskHandler;
    private final TaskRegistry taskRegistry;

    public TaskScheduler(TaskHandler taskHandler, TaskRegistry taskRegistry) {
        this.taskHandler = taskHandler;
        this.taskRegistry = taskRegistry;
    }

    @Override
    public void register(Task task) {
        taskRegistry.register(task);
        scheduledTasks.add(task);
    }

    @Override
    public void registerAll(List<Task> tasks) {
        tasks.forEach(task -> {
            taskRegistry.register(task);
            scheduledTasks.add(task);
        });
    }

    @Override
    public void executeAll() throws TaskException {
        if (hasCircularDependency()) {
            throw new TaskException(TaskException.ErrorType.CIRCULAR_DEPENDENCY, "Circular dependency detected");
        }

        while (!scheduledTasks.isEmpty()) {
            execute(scheduledTasks.poll());
        }
    }

    @Override
    public void execute(Task task) throws TaskException {
        if (hasCircularDependency()) {
            throw new TaskException(task.getId(), TaskException.ErrorType.CIRCULAR_DEPENDENCY);
        }

        if (task.getState() == TaskState.SUCCESS) return;

        for (UUID dependency : task.getDependencies()) {
            Task dependencyTask = taskRegistry.get(dependency).orElseThrow(() -> new TaskException(dependency, TaskException.ErrorType.TASK_NOT_FOUND));
            if (dependencyTask.getState() != TaskState.SUCCESS) {
                execute(dependencyTask);
            }
        }
        taskHandler.executeTask(task);
    }

    @Override
    public void execute(UUID id) throws TaskException {
        Task task = taskRegistry.get(id).orElseThrow(() -> new TaskException(id, TaskException.ErrorType.TASK_NOT_FOUND));
        execute(task);
    }

    @Override
    public boolean hasCircularDependency() {
        Set<UUID> visited = new HashSet<>();
        Set<UUID> stack = new HashSet<>();
        return taskRegistry.getAllTasks().stream().anyMatch(t -> detectCycle(t, visited, stack));
    }

    private boolean detectCycle(Task task, Set<UUID> visited, Set<UUID> stack) {
        if (stack.contains(task.getId())) return true;
        if (visited.contains(task.getId())) return false;

        visited.add(task.getId());
        stack.add(task.getId());

        for (UUID depId : task.getDependencies()) {
            Task dep = taskRegistry.get(depId).orElse(null);
            if (dep != null && detectCycle(dep, visited, stack)) return true;
        }
        stack.remove(task.getId());
        return false;
    }

    @Override
    public void clear() {
        scheduledTasks.clear();
    }

    @Override
    public void close() throws Exception {
        clear();
    }
}
