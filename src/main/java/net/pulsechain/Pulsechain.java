package net.pulsechain;

import net.pulsechain.scheduler.TaskScheduler;
import net.pulsechain.task.handler.TaskHandler;
import net.pulsechain.task.registry.TaskRegistry;

public class Pulsechain {

    public static TaskScheduler createTaskScheduler() {
        TaskHandler taskHandler = new TaskHandler();
        TaskRegistry taskRegistry = new TaskRegistry();
        return new TaskScheduler(taskHandler, taskRegistry);
    }

}
