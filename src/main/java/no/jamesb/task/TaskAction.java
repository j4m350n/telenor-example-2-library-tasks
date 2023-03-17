package no.jamesb.task;

public interface TaskAction<T> {
	T run() throws Exception;
}