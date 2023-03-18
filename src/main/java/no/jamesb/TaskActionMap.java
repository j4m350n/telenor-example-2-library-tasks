package no.jamesb;

public interface TaskActionMap<V, T> {
	V run(T value) throws Exception;
}
