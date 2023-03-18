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

	@Test
	public void successAnd() {
		TaskActionAnd<Boolean, Integer> isOne = value -> value == 1;
		Assertions.assertTrue(Task.complete(1).and(isOne).await());
		Assertions.assertFalse(Task.complete(2).and(isOne).await());
		Assertions.assertFalse(Task.complete(0).and(isOne).await());
	}

	@Test
	public void failureAnd() {
		Exception e = new Exception("hello");
		Task<Boolean> task = Task.<Integer>fail(e)
			.and(value -> value == 1);
		TaskResult<Boolean> result = task.waitForResult();
		Assertions.assertTrue(result.didThrow);
		Assertions.assertNull(result.value);
		Assertions.assertEquals(e, result.exception);
	}

	@Test
	public void successOr() {
		Assertions.assertDoesNotThrow(() -> Assertions.assertEquals(1, Task.complete(1).or(ex -> -1).await()));
	}

	@Test
	public void failureOr() {
		Assertions.assertDoesNotThrow(() -> Assertions.assertEquals(-1, Task.fail(new Exception("hello")).or(ex -> {
			Assertions.assertThrows(
				Exception.class,
				() -> {
					throw ex;
				},
				"hello"
			);
			return -1;
		}).await()));
	}
}