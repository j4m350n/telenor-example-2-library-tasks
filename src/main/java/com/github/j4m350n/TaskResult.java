package com.github.j4m350n;

@SuppressWarnings("ClassCanBeRecord")
public class TaskResult<T> {
	public static <T> TaskResult<T> success(T value) {
		return new TaskResult<>(false, null, value);
	}

	public static <T> TaskResult<T> failure(Exception exception) {
		return new TaskResult<>(true, exception, null);
	}

	public final boolean didThrow;
	public final Exception exception;
	public final T value;

	public TaskResult(boolean didThrow, Exception exception, T value) {
		if (didThrow && exception == null) {
			throw new NullPointerException("Could not instantiate TaskResult: the thrown exception cannot be null!");
		} else if (!didThrow && value == null) {
			throw new NullPointerException("Could not instantiate TaskResult: The returned action value cannot be null!");
		}

		this.didThrow = didThrow;
		this.exception = exception;
		this.value = value;
	}
}
