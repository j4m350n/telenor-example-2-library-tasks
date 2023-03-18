package no.jamesb.task;

public interface TaskResultAction<T> {
	TaskResult<T> run() throws Exception;
}