package com.github.j4m350n;

public interface TaskResultAction<T> {
	TaskResult<T> run() throws Exception;
}