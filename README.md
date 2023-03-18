# Awaitable Tasks

This library brings a convenient, easy and beautiful way to run synchronous
blocking code in non-blocking asynchronous threads and interact with the results
of the tasks.

> **WARNING**: `null` is not allowed anywhere, it will throw, deal with it! :)

<!-- @formatter:off -->
```java
new Task<User>(() -> findUserSomehow())
  .or(ex -> {
    if (ex instanceof NotFoundException) {
      return "user-not-found@example.com";
    }
    throw ex;
  })
  .map(user -> {
    String email = user.getEmail()
    if (email == null) {
      throw new Exception("Email is null!");
    }
  })
  .and(email -> sendUserEmailSomehow("Hello world"))
  .await()
  // ...
```
<!-- @formatter:on -->
