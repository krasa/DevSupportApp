package krasa.build.backend.domain;

public enum Status {
	PENDING(false),
	RUNNING(false),
	SUCCESS(true),
	FAILED(true),
	EXCEPTION(true),
	KILLED(true),
	DISCONNECTED(true), ;

	boolean end;

	Status(boolean end) {
		this.end = end;
	}

	public boolean isEnd() {
		return end;
	}
}
