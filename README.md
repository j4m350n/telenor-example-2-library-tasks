# Awaitable Tasks

> **TODO:** Write readme

High level overview plan:

- `Task<T>`: An object that creates an awaitable thread, which can also return a
  value `T`.
  - static methods
    - [x] `<T> Task<T> complete(T value)`: Create and automatically complete a
      task with the given value.
    - [x] `<T> Task<T> fail(Exception exception)`: Create and automatically
      throw an exception, causing the Task to
      fail.
    - [x] `<T> Task<List<T>> awaitAll(List<Task<T>> tasks)`: Wait for all
      provided tasks to complete. **Method renamed to all.**
  - instance methods
    - [x] `T await()`: Wait for the task to finish, returning the *completed*
      value. Catches and re-throws any exception
      that the task throws.
    - [ ] `<V> Task<V> and(TaskActionAnd<V, T> action)`: Run the
      provided `action` on the value returned by the previous
      task. If the previous task throws, the previous value will be returned
      and `action` will not be called.
      - **Note**: `and()` has the ability to mutate the `Task<T>` type over each
        call, either by inferring the value on
        return, or by strictly setting it on call: `task.<NewType>and(...)`.
      - **Example 1**: Calling `and()` on task that completes.
        ```java
        Integer value = Task.complete(123)
          .and(previousValue -> previousValue * 2)
          .await();
        Assertion.assertEquals(246, value);
        ```
      - **Example 2**: Calling `and()` on task that throws.
        ```java
        Assertion.assertThrows(
          Exception.class,
          () -> {
           Task.complete(123)
            .and(previousValue -> previousValue * 2)
            .<Integer>and(previousValue -> {
              throw new Exception("hello");
            })
            .await();
          },
          "hello"
        );
        ```
    - [ ] `Task<T> or(TaskActionOr<T> action)`: Run the provided `action` on the
      exception thrown by the previous task.
      If the previous task doesn't throw, return its value instead.
      - **Note**: Because this function returns the previous task value when the
        previous task doesn't throw any
        exceptions, the `action` of the new task created by this function will
        not be able to change the type
        of `Task<T>`.
      - **Example 1**: Gracefully handle any exceptions thrown in the
        Task-chain.
        ```java
        Integer value = Task.complete(123)
          .and(previousValue -> previousValue * 2)
          .<Integer>and(previousValue -> {
          })
          .or(exception -> {
            // Optionally do something with `exception`
            // and return a fallback value.
            return -1;
          })
          .await();
        Assertion.assertEquals(-1, value);
        ```

## Research

The purpose of this class is to make it easier to write non-blocking code. The
way this will be achieved is by running
the lambdas provided in a thread. Upon researching thread usage in Java I
discovered that stack traces are even
shittier. I need to find a way to make these stack traces better. 