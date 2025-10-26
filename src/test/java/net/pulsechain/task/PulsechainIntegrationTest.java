package net.pulsechain.task;

import net.pulsechain.scheduler.TaskScheduler;
import net.pulsechain.task.exception.TaskException;
import net.pulsechain.task.handler.TaskHandler;
import net.pulsechain.task.handler.result.TaskResult;
import net.pulsechain.task.registry.TaskRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PulsechainIntegrationTest {

    private Task taskA;
    private Task taskB;
    private Task taskC;
    private Task taskD;

    private TaskScheduler taskScheduler;

    @BeforeEach
    public void setUp() {
        taskA = new Task("TaskA") {
            @Override
            public void execute() {
                System.out.println("Executing TaskA");
            }
        };

        taskB = new Task("TaskB") {
            @Override
            public void execute() {
                System.out.println("Executing TaskB");
            }
        };

        taskC = new Task("TaskC") {
            @Override
            public void execute() {
                System.out.println("Executing TaskC");
            }
        };

        taskD = new Task("TaskD") {
            @Override
            public void execute() {
                System.out.println("Executing TaskD");
            }
        };

        TaskHandler taskHandler = new TaskHandler();
        TaskRegistry taskRegistry = new TaskRegistry();
        taskScheduler = new TaskScheduler(taskHandler, taskRegistry);
    }

    @Test
    void testSuccessfulExecution() throws TaskException {
        List<TaskResult> taskResults = registerAndExecuteAll();

        assertEquals(4, taskResults.size());
        assertEquals(TaskState.SUCCESS, taskResults.get(0).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(1).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(2).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(3).getFinalState());
    }


    @Test
    void testDependencyFailure() throws TaskException {
        taskB = new Task("TaskB") {
            @Override
            public void execute() {
                throw new NullPointerException("Generic NullPointerException on task");
            }
        };

        List<TaskResult> taskResults = registerAndExecuteAll();

        assertEquals(4, taskResults.size());
        assertEquals(TaskState.SUCCESS, taskResults.get(0).getFinalState());
        assertEquals(TaskState.FAILED, taskResults.get(1).getFinalState());
        assertEquals("Generic NullPointerException on task", taskResults.get(1).getError().getMessage().trim());
        assertEquals(TaskState.BLOCKED, taskResults.get(2).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(3).getFinalState());
    }

    @Test
    void testDependencyFailureExceedingRetry() throws TaskException {
        taskB = new Task("TaskB") {
            int counter = 3;
            @Override
            public void execute() {
                counter--;

                if (counter > 0) {
                    throw new NullPointerException("Generic NullPointerException on task");
                } else {
                    System.out.println("Executing TaskB");
                }
            }
        }.withMaxRetries(2);

        List<TaskResult> taskResults = registerAndExecuteAll();

        assertEquals(4, taskResults.size());
        assertEquals(TaskState.SUCCESS, taskResults.get(0).getFinalState());
        assertEquals(TaskState.FAILED, taskResults.get(1).getFinalState());
        assertEquals("Generic NullPointerException on task", taskResults.get(1).getError().getMessage().trim());
        assertEquals(TaskState.BLOCKED, taskResults.get(2).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(3).getFinalState());
    }

    @Test
    void testDependencySuccessWithRetry() throws TaskException {
        taskB = new Task("TaskB") {
            int counter = 3;
            @Override
            public void execute() {
                counter--;

                if (counter > 0) {
                    throw new NullPointerException("Generic NullPointerException on task");
                } else {
                    System.out.println("Executing TaskB");
                }
            }
        }.withMaxRetries(3);

        List<TaskResult> taskResults = registerAndExecuteAll();

        assertEquals(4, taskResults.size());
        assertEquals(TaskState.SUCCESS, taskResults.get(0).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(1).getFinalState());
        assertEquals(2, taskResults.get(1).getRetryCount());
        assertEquals(TaskState.SUCCESS, taskResults.get(2).getFinalState());
        assertEquals(TaskState.SUCCESS, taskResults.get(3).getFinalState());
    }

    @Test
    void testCircularDependency() throws TaskException {
        taskScheduler.register(taskA);
        taskA.addDependency(taskC);

        taskB.addDependency(taskA);
        taskScheduler.register(taskB);

        taskC.addDependencies(taskA, taskB);
        taskScheduler.register(taskC);

        TaskException exception = assertThrows(TaskException.class, () -> taskScheduler.executeAll());

        assertEquals(TaskException.ErrorType.CIRCULAR_DEPENDENCY, exception.getErrorType());
        assertEquals("Circular dependency detected", exception.getMessage());
    }

    private List<TaskResult> registerAndExecuteAll() throws TaskException {
        taskScheduler.register(taskA);

        taskB.addDependency(taskA);
        taskScheduler.register(taskB);

        taskC.addDependencies(taskA, taskB);
        taskScheduler.register(taskC);

        taskScheduler.register(taskD);

        return taskScheduler.executeAll();
    }



}
