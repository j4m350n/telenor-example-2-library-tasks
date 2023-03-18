package no.jamesb;

public interface TaskResultAction<T> {
	TaskResult<T> run() throws Exception;
}