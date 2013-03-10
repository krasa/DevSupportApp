package krasa.build.backend.execution.process;

import krasa.build.backend.execution.ProcessStatus;

public interface Process extends Runnable {
	public void stop();

	public ProcessStatus getStatus();

}
