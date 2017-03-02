package krasa.build.backend.execution;

import com.google.common.base.Objects;

import krasa.build.backend.domain.Status;

public class ProcessStatus {
	private Status status;
	private Throwable exception;

	public ProcessStatus() {
	}

	public ProcessStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public Throwable getException() {
		return exception;
	}

	public boolean isAlive() {
		return status == Status.RUNNING || status == Status.PENDING;
	}

	public boolean isRunning() {
		return status == Status.RUNNING;
	}

	public void setStatus(Status status1) {
		this.status = status1;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", status).add("exception", exception).toString();
	}
}
