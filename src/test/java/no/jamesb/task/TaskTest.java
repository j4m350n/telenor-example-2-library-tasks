package no.jamesb.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

	@Test
	public void constructorAcceptsTaskResult() {
		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(TaskResult.success(0));
			Assertions.assertNotNull(task._result.get());
			Assertions.assertEquals(0, task.await());
		});

		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = Task.complete(0);
			Assertions.assertNotNull(task._result.get());
			Assertions.assertEquals(0, task.await());
		});

		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(TaskResult.failure(new Exception("hello")));
			Assertions.assertNotNull(task._result.get());
			Assertions.assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
		});

		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = Task.fail(new Exception("hello"));
			Assertions.assertNotNull(task._result.get());
			Assertions.assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
		});
	}

	@Test
	public void constructorAcceptsAction() {
		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> {
				Thread.sleep(100);
				return 0;
			});
			Assertions.assertNull(task._result.get());
			Assertions.assertEquals(0, task.await());
			Assertions.assertNotNull(task._result.get());
		});

		Assertions.assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> {
				throw new Exception("hello");
			});
			Assertions.assertNull(task._result.get());
			Assertions.assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
			Assertions.assertNotNull(task._result.get());
		});
	}

	@Test
	public void waitForResultThreadInterruptionShouldDoNothing() {
		Assertions.assertDoesNotThrow(() -> {
			Thread currentThread = Thread.currentThread();
			Task<Integer> task = new Task<>(() -> {
				Thread.sleep(100);
				currentThread.interrupt();
				Thread.sleep(100);
				return 0;
			});
			Assertions.assertNull(task._result.get());
			Assertions.assertEquals(0, task.await());
			Assertions.assertNotNull(task._result.get());
		});
	}
}