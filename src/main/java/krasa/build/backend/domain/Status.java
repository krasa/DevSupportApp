package krasa.build.backend.domain;

public enum Status {
	PENDING,
	@Deprecated
	IN_PROGRESS,
	RUNNING,
	SUCCESS,
	FAILED,
	EXCEPTION,
	KILLED,
	DISCONNECTED,
}
