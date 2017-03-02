package krasa.build.backend.execution.process;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.execution.ProcessStatus;

public interface ProcessStatusListener {

	void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus);

}
