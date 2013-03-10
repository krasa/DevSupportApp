package krasa.build.backend.execution.process;

import krasa.build.backend.execution.ProcessStatus;

public interface ProcessResultListener {
	void onResult(ProcessStatus processStatus);

}
