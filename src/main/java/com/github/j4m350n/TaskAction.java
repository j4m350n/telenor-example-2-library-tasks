package com.github.j4m350n;

public interface TaskAction<T> {
	T run() throws Exception;
}