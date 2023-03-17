package no.jamesb.task;

public interface TaskActionAnd<V, T> {
	V run(T value) throws Exception;
}
