package com.github.j4m350n;

public interface TaskActionOr<T> {
	T run(Exception exception) throws Exception;
}
