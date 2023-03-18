# Awaitable Tasks

## Research

The purpose of this class is to make it easier to write non-blocking code. The
way this will be achieved is by running
the lambdas provided in a thread. Upon researching thread usage in Java I
discovered that stack traces are even
shittier. I need to find a way to make these stack traces better.

A possibility for this problem is to capture the stack trace before starting the
thread. Any exceptions thrown inside the thread will get those stack trace lines
from when before the thread was started.