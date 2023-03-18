package no.jamesb;

public interface TaskActionOr<T> {
	T run(Exception exception) throws Exception;
}
