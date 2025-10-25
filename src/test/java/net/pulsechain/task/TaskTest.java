package net.pulsechain.task;

import net.pulsechain.task.exception.TaskException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    static class TestTask extends Task {
        public TestTask(String name) {
            super(name);
        }

        public TestTask(String name, List<UUID> dependencies) {
            super(name, dependencies);
        }

        @Override
        public void execute() {
            // No-op for testing
        }
    }

    @Test
    void testAddDependency_AddsValidDependency() throws TaskException {
        UUID validDependency = UUID.randomUUID();
        Task task = new TestTask("Test Task");

        task.addDependency(validDependency);

        assertTrue(task.getDependencies().contains(validDependency));
        assertEquals(1, task.getDependencies().size());
    }

    @Test
    void testAddDependency_ThrowsForSelfDependency() {
        Task task = new TestTask("Test Task");

        TaskException exception = assertThrows(TaskException.class, () ->
                task.addDependency(task.getId())
        );

        assertEquals(TaskException.ErrorType.SELF_DEPENDENCY, exception.getErrorType());
        assertEquals("Dependency cannot be the same as the task itself", exception.getMessage());
    }

    @Test
    void testAddDependency_ThrowsForNullDependency() {
        Task task = new TestTask("Test Task");

        TaskException exception = assertThrows(TaskException.class, () ->
                task.addDependency(null)
        );

        assertEquals(TaskException.ErrorType.SELF_DEPENDENCY, exception.getErrorType());
        assertEquals("Dependency cannot be the same as the task itself", exception.getMessage());
    }

    @Test
    void testAddDependency_DoesNotAllowDuplicateDependencies() throws TaskException {
        UUID dependency = UUID.randomUUID();
        Task task = new TestTask("Test Task");

        task.addDependency(dependency);
        task.addDependency(dependency);

        assertTrue(task.getDependencies().contains(dependency));
        assertEquals(2, task.getDependencies().size(), "Duplicates are allowed (testing per provided requirements)");
    }
}