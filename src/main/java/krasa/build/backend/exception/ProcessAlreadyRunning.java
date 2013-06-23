package krasa.build.backend.exception;

import krasa.build.backend.domain.BuildJob;

public class ProcessAlreadyRunning extends RuntimeException {
	private BuildJob progress;

	public ProcessAlreadyRunning(BuildJob progress) {
		this.progress = progress;
	}

	public BuildJob getProgress() {
		return progress;
	}

	public void setProgress(BuildJob progress) {
		this.progress = progress;
	}
}
