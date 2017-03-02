package krasa.build.backend.exception;

import krasa.build.backend.domain.BuildJob;

public class ProcessAlreadyRunning extends RuntimeException {
	private BuildJob progress;

	public ProcessAlreadyRunning(BuildJob job) {
		super("already building " + job.getBuildableComponent().getName());
		this.progress = job;
	}

	public BuildJob getProgress() {
		return progress;
	}

	public void setProgress(BuildJob progress) {
		this.progress = progress;
	}

}
