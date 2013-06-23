package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.List;

import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;

import com.google.common.base.Objects;

public class DummyProcess extends SshBuildProcess {

	public DummyProcess(ProcessLog stringBufferTail, List<String> command) {
		super(stringBufferTail, command);
	}

	@Override
	protected int doWork() throws IOException {
		int i = 0;
		while (i < 50 && processStatus.getStatus() != Status.KILLED) {
			processLog.append(String.valueOf(++i)).newLine();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (processStatus.getStatus() == Status.IN_PROGRESS) {
			processStatus.setStatus(Status.SUCCESS);
		}
		return 0;
	}

	@Override
	protected void onFinally() {
		log.info("process complete. " + this.toString());
		notifyListeners();
	}

	@Override
	public ProcessStatus getStatus() {
		return processStatus;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", processStatus).toString();
	}
}
