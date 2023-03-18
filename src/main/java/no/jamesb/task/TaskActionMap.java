package no.jamesb.task;

public interface TaskActionMap<V, T> {
	V run(T value) throws Exception;
}
