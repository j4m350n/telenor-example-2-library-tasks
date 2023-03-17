package no.jamesb.task;

public class TaskResult<T> {
	public static <T> TaskResult<T> success(T value) {
		return new TaskResult<>(false, null, value);
	}

	public static <T> TaskResult<T> failure(Exception exception) {
		return new TaskResult<>(true, exception, null);
	}

	public final boolean didThrow;
	public final Exception exception;
	public final T returnValue;

	public TaskResult(boolean didThrow, Exception exception, T value) {
		this.didThrow = didThrow;
		this.exception = exception;
		this.returnValue = value;

		if (!this.didThrow && this.returnValue == null) {
			throw new NullPointerException("The provided return value cannot be null when no exception was thrown!");
		} else if (this.didThrow && this.exception == null) {
			throw new NullPointerException("The exception cannot be null when an error was expected!");
		}
	}
}
