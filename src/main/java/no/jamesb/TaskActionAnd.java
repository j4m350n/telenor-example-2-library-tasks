package no.jamesb;

public interface TaskActionAnd<V, T> {
	Task<V> run(T value) throws Exception;
}
