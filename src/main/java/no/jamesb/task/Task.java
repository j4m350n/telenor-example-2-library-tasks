package no.jamesb.task;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Task<T> {

	/**
	 * Create a new task and automatically complete it with the value.
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 *   Integer value = Task.complete(123).await();
	 *   Assertion.assertEquals(123, value);
	 * }
	 * </pre></code>
	 *
	 * @param value The value returned by the task.
	 * @param <T>   The type of the value returned by the task.
	 * @return Returns the task.
	 */
	public static <T> Task<T> complete(T value) {
		return null;
	}

	/**
	 * Create a new task and automatically throw the provided exception
	 * in it.
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 * Assertion.assertThrows(
	 *   Exception.class,
	 *   Task.<Integer>fail(new Exception("hello")).await(),
	 *   "hello"
	 * );
	 * }
	 * </pre></code>
	 *
	 * @param exception The exception to throw.
	 * @param <T>       The type of the expected value in the task.
	 * @return Returns the task.
	 */
	public static <T> Task<T> fail(Exception exception) {
		return null;
	}

	/**
	 * Map all the provided tasks into their completed values. If any of
	 * the provided tasks throws an exception, this entire task will
	 * fail as well.
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 *   List<Task<Integer>> tasks = new List<>();
	 *   list.add(Task.resolve(1));
	 *   list.add(Task.resolve(2));
	 *   list.add(Task.resolve(3));
	 *   List<Integer> result = Task.<Integer>all(tasks).await();
	 *
	 *   Assertions.assertEquals(
	 *     new Integer[]{1, 2, 3},
	 *     result.toArray(new Integer[3])
	 *   );
	 * }
	 * </pre></code>
	 *
	 * @param tasks The tasks to wait for.
	 * @param <T>   The type of the values returned by the provided
	 *              tasks.
	 * @return A task that completes with a list of the returned values
	 * by the provided tasks.
	 */
	public static <T> Task<List<T>> all(List<Task<T>> tasks) {
		return null;
	}

	protected AtomicReference<TaskResult<T>> _result = new AtomicReference<>(null);

	public Task(TaskAction<T> action) {

	}

	/**
	 * Wait for the task complete. If the task throws any exceptions the
	 * exception will be re-thrown in a <code>RuntimeException</code>.
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 *   Integer result = new Task<Integer>(() -> {
	 *     Thread.sleep(1000);
	 *     return 123;
	 *   }).await();
	 *
	 *   Assertions.assertEquals(123, result);
	 * }
	 * </pre></code>
	 *
	 * @return The completed value.
	 */
	public T await() {
		TaskResult<T> result = this.waitForResult();
		if (result.didThrow) {
			throw new RuntimeException(result.exception);
		}
		return result.value;
	}

	/**
	 * <p>
	 * Run the provided <code>action</code> with the previous computed
	 * value. If the task fails then the provided action will not be
	 * executed.
	 * </p>
	 * <p>
	 * This method has the ability to change/mutate the type returned
	 * by the task over each call, either by inferring
	 * ({@code task.and(previousValue -> newValue)} or by strictly
	 * typing the new task type ({@code task.<Integer>and(previousValue -> newValue)}
	 * </p>
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 *   Integer result = Task.complete(123)
	 *     .and(previousValue -> previousValue * 2)
	 *     .await();
	 *
	 *   Assertions.asserEquals(246, result);
	 * }
	 * </pre></code>
	 *
	 * @param action The <i>and()</i> action.
	 * @param <V>    The new task return type.
	 * @return The new task.
	 */
	public <V> Task<V> and(TaskActionAnd<V, T> action) {
		return null;
	}

	/**
	 * <p>
	 * Run the provided <code>action</code> with the exception thrown by the
	 * previous task. If the previous task doesn't throw an exception return its
	 * value instead.
	 * </p>
	 * <p>
	 * <b>Note:</b> Because this function possibly returns the previous value, the
	 * new value must also share the same type as the previous value, thus, the
	 * Task type becomes immutable and cannot change over time with <code>or</code>
	 * calls. You can, however, use {@link Task#and(TaskActionAnd)} to mutate the
	 * value and type of the task.
	 * </p>
	 *
	 * <code class="language-java"><pre>
	 * {@code
	 *   Integer result = Task.complete(123)
	 *     .and(previousValue -> previousValue * 2)
	 *     .and(previousValue -> {
	 *       throw new Exception("hello");
	 *     })
	 *     .or(exception -> {
	 *       // Optionally do something with the exception and return
	 *       // a fallback value.
	 *       return -1;
	 *     })
	 *     .await();
	 *
	 *   Assertions.asserEquals(-1, result);
	 * }
	 * </pre></code>
	 *
	 * @param action The <i>and()</i> action.
	 * @return The new task.
	 */
	public Task<T> or(TaskActionOr<T> action) {
		return null;
	}

	protected TaskResult<T> waitForResult() {
		TaskResult<T> result = this._result.get();
		while (result == null) {
			try {
				wait();
			} catch (InterruptedException _e) {
				// ignored
			}
			result = this._result.get();
		}
		return result;
	}

}
