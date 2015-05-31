package krasa.build.backend.execution.process;

import javax.validation.constraints.NotNull;

import krasa.build.backend.execution.ProcessStatus;

public interface Process {
	void run();

	void stop(String reason);

	ProcessStatus getStatus();

	@NotNull
	ProcessLog getProcessLog();

}
