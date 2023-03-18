package com.github.j4m350n;

public interface TaskActionAnd<V, T> {
	Task<V> run(T value) throws Exception;
}
