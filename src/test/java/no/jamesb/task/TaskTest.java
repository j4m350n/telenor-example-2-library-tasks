package no.jamesb.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

	@Test
	public void awaitAll() {
		List<Task<Integer>> tasks = new ArrayList<>();
		tasks.add(Task.complete(1));
		tasks.add(Task.complete(2));
		tasks.add(Task.complete(3));

		Assertions.assertArrayEquals(
			new Integer[]{1, 2, 3},
			Task.all(tasks).await().toArray(new Integer[3])
		);
	}
}