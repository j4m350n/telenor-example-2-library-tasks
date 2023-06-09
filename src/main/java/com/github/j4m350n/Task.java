package com.github.j4m350n;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Task<T> {

	/**
	 * <p>Create a new task and automatically complete it with the value.</p>
	 *
	 * <pre>{@code
	 *   Integer value = Task.complete(123).await();
	 *   Assertion.assertEquals(123, value);
	 * }</pre>
	 *
	 * @param value The value returned by the task.
	 * @param <T>   The type of the value returned by the task.
	 * @return Returns the task.
	 */
	public static <T> Task<T> complete(T value) {
		return new Task<>(TaskResult.success(value));
	}

	/**
	 * <p>Create a new task and automatically throw the provided exception
	 * in it.
	 * </p>
	 *
	 * <pre>{@code
	 * Assertion.assertThrows(
	 *   Exception.class,
	 *   Task.<Integer>fail(new Exception("hello")).await(),
	 *   "hello"
	 * );
	 * }</pre>
	 *
	 * @param exception The exception to throw.
	 * @param <T>       The type of the expected value in the task.
	 * @return Returns the task.
	 */
	public static <T> Task<T> fail(Exception exception) {
		return new Task<>(TaskResult.failure(exception));
	}

	/**
	 * <p>
	 * Map all the provided tasks into their completed values. If any of
	 * the provided tasks throws an exception, this entire task will
	 * fail as well.
	 * </p>
	 *
	 * <pre>{@code
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
	 * </pre>
	 *
	 * @param tasks The tasks to wait for.
	 * @param <T>   The type of the values returned by the provided
	 *              tasks.
	 * @return A task that completes with a list of the returned values
	 * by the provided tasks.
	 */
	public static <T> Task<List<T>> all(List<Task<T>> tasks) {
		return new Task<>(() -> tasks.stream().map(Task::await).toList());
	}

	protected final AtomicReference<TaskResult<T>> _result = new AtomicReference<>(null);
	protected final Thread _thread;

	public Task(TaskResult<T> result) {
		this._result.set(result);
		this._thread = null;
	}

	public Task(TaskAction<T> action) {
		final StackTraceElement[] _mainStack = this.getStackTrace();
		this._thread = new Thread(() -> {
			synchronized (this) {
				try {
					this._result.set(TaskResult.success(action.run()));
				} catch (Exception exception) {
					{
						StackTraceElement[] stack = exception.getStackTrace();
						StackTraceElement[] newStack = new StackTraceElement[stack.length + _mainStack.length];
						System.arraycopy(stack, 0, newStack, 0, stack.length);
						System.arraycopy(_mainStack, 0, newStack, stack.length, _mainStack.length);
						exception.setStackTrace(newStack);
					}
					this._result.set(TaskResult.failure(exception));
				} finally {
					notifyAll();
				}
			}
		});
		this._thread.start();
	}

	public Task(TaskResultAction<T> action) {
		final StackTraceElement[] _mainStack = this.getStackTrace();
		this._thread = new Thread(() -> {
			synchronized (this) {
				try {
					TaskResult<T> result = action.run();
					if (result.didThrow) {
						{
							StackTraceElement[] stack = result.exception.getStackTrace();
							StackTraceElement[] newStack = new StackTraceElement[stack.length + _mainStack.length];
							System.arraycopy(stack, 0, newStack, 0, stack.length);
							System.arraycopy(_mainStack, 0, newStack, stack.length, _mainStack.length);
							result.exception.setStackTrace(newStack);
						}
					}
					this._result.set(result);
				} catch (Exception exception) {
					{
						StackTraceElement[] stack = exception.getStackTrace();
						StackTraceElement[] newStack = new StackTraceElement[stack.length + _mainStack.length];
						System.arraycopy(stack, 0, newStack, 0, stack.length);
						System.arraycopy(_mainStack, 0, newStack, stack.length, _mainStack.length);
						exception.setStackTrace(newStack);
					}
					this._result.set(TaskResult.failure(exception));
				} finally {
					notifyAll();
				}
			}
		});
		this._thread.start();
	}

	private StackTraceElement[] getStackTrace() {
		final StackTraceElement[] _mainStack;
		{
			StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
			_mainStack = Arrays.copyOfRange(tmp, 3, tmp.length);
		}
		return _mainStack;
	}

	/**
	 * <p>
	 * Wait for the task complete. If the task throws any exceptions the
	 * exception will be re-thrown in a <code>RuntimeException</code>.
	 * </p>
	 *
	 * <pre>{@code
	 *   Integer result = new Task<Integer>(() -> {
	 *     Thread.sleep(1000);
	 *     return 123;
	 *   }).await();
	 *
	 *   Assertions.assertEquals(123, result);
	 * }</pre>
	 *
	 * @return The completed value.
	 */
	public T await() {
		final TaskResult<T> result = this.waitForResult();
		if (result.didThrow) {
			throw new RuntimeException(result.exception);
		}
		return result.value;
	}

	/**
	 * <p>
	 * Take the result and map it into a new awaitable task.
	 * </p>
	 *
	 * <pre>{@code
	 *   Integer result = Task.complete(123)
	 *     .and(previousValue -> Task.complete(previousValue * 2))
	 *     .await();
	 *
	 *   Assertions.asserEquals(246, result);
	 * }</pre>
	 *
	 * @param action The <i>and()</i> action.
	 * @param <V>    The new task return type.
	 * @return The new task.
	 */
	public <V> Task<V> and(TaskActionAnd<V, T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (result.didThrow) return TaskResult.failure(result.exception);
			return action.run(result.value).waitForResult();
		});
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
	 * ({@code task.map(previousValue -> newValue)} or by strictly
	 * typing the new task type ({@code task.<Integer>map(previousValue -> newValue)}
	 * </p>
	 *
	 * <pre>{@code
	 *   Integer result = Task.complete(123)
	 *     .map(previousValue -> previousValue * 2)
	 *     .await();
	 *
	 *   Assertions.asserEquals(246, result);
	 * }</pre>
	 *
	 * @param action The <i>map()</i> action.
	 * @param <V>    The new task return type.
	 * @return The new task.
	 */
	public <V> Task<V> map(TaskActionMap<V, T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (result.didThrow) throw result.exception;
			return action.run(result.value);
		});
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
	 * calls. You can, however, use {@link Task#map(TaskActionMap)} to mutate the
	 * value and type of the task.
	 * </p>
	 *
	 * <pre>{@code
	 *   Integer result = Task.complete(123)
	 *     .map(previousValue -> previousValue * 2)
	 *     .map(previousValue -> {
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
	 * }</pre>
	 *
	 * @param action The <i>or()</i> action.
	 * @return The new task.
	 */
	public Task<T> or(TaskActionOr<T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (!result.didThrow) return result.value;
			return action.run(result.exception);
		});
	}

	protected synchronized TaskResult<T> waitForResult() {
		TaskResult<T> result = this._result.get();
		while (result == null) {
			try {
				wait();
			} catch (InterruptedException _e) {
				Thread.currentThread().interrupt();
			}
			result = this._result.get();
		}
		return result;
	}

}
