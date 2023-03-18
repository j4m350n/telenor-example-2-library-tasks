package com.github.j4m350n;

public interface TaskActionMap<V, T> {
	V run(T value) throws Exception;
}
