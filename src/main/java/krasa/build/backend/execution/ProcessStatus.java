package krasa.build.backend.execution;

import krasa.build.backend.domain.Status;

public class ProcessStatus {
	private Status status;
	private Exception exception;

	public Status getStatus() {
		return status;
	}

	public Exception getException() {
		return exception;
	}

	public boolean isAlive() {
		return status == Status.IN_PROGRESS;
	}

	public void setStatus(Status status1) {
		this.status = status1;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public static ProcessStatus alive(boolean alive) {
		ProcessStatus processStatus = new ProcessStatus();
		processStatus.setStatus(alive ? Status.IN_PROGRESS : Status.SUCCESS);
		return processStatus;
	}
}
