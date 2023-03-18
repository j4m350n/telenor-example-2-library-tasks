package no.jamesb;

public interface TaskAction<T> {
	T run() throws Exception;
}