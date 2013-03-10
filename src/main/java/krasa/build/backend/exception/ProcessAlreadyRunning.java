package krasa.build.backend.exception;

import krasa.build.backend.execution.adapter.ProcessAdapter;

public class ProcessAlreadyRunning extends RuntimeException {
	private ProcessAdapter progress;

	public ProcessAlreadyRunning(ProcessAdapter progress) {
		this.progress = progress;
	}

	public ProcessAdapter getProgress() {
		return progress;
	}

	public void setProgress(ProcessAdapter progress) {
		this.progress = progress;
	}
}
