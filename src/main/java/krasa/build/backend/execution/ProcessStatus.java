package krasa.build.backend.execution;

import krasa.build.backend.domain.Status;

import com.google.common.base.Objects;

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

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", status).add("exception", exception).toString();
	}
}
