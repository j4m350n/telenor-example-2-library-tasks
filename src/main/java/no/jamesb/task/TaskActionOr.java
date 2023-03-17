package no.jamesb.task;

public interface TaskActionOr<T> {
	T run(Exception exception) throws Exception;
}
