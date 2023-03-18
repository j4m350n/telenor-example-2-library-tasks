package no.jamesb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskResultTest {

	@Test
	public void validSuccessResult() {
		Assertions.assertDoesNotThrow(() -> {
			TaskResult<Integer> result = new TaskResult<>(false, null, 0);
			Assertions.assertFalse(result.didThrow);
			Assertions.assertNull(result.exception);
			Assertions.assertEquals(0, result.value);
		});
		Assertions.assertDoesNotThrow(() -> {
			TaskResult<Integer> result = TaskResult.success(0);
			Assertions.assertFalse(result.didThrow);
			Assertions.assertNull(result.exception);
			Assertions.assertEquals(0, result.value);
		});
	}

	@Test
	public void validFailureResult() {
		Assertions.assertDoesNotThrow(() -> {
			TaskResult<Integer> result = new TaskResult<>(true, new Exception("hello"), null);
			Assertions.assertTrue(result.didThrow);
			Assertions.assertNull(result.value);
			Assertions.assertThrows(
				Exception.class,
				() -> {
					throw result.exception;
				},
				"hello"
			);
		});
		Assertions.assertDoesNotThrow(() -> {
			TaskResult<Integer> result = TaskResult.failure(new Exception("hello"));
			Assertions.assertTrue(result.didThrow);
			Assertions.assertNull(result.value);
			Assertions.assertThrows(
				Exception.class,
				() -> {
					throw result.exception;
				},
				"hello"
			);
		});
	}

	@Test
	public void didNotThrowValueIsNull() {
		Assertions.assertThrows(
			NullPointerException.class,
			() -> TaskResult.success(null),
			"Could not instantiate TaskResult: The returned action value cannot be null!"
		);
	}

	@Test
	public void didThrowExceptionIsNull() {
		Assertions.assertThrows(
			NullPointerException.class,
			() -> new TaskResult<Integer>(true, null, null),
			"Could not instantiate TaskResult: the thrown exception cannot be null!"
		);
	}

}