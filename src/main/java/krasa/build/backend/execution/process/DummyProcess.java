package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.List;

import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.StringBufferTail;

public class DummyProcess extends SshBuildProcess {

	protected volatile Status status = Status.IN_PROGRESS;

	public DummyProcess(StringBufferTail stringBufferTail, List<String> command) {
		super(stringBufferTail, command);
	}

	@Override
	protected int doWork() throws IOException {
		int i = 0;
		while (i < 50) {
			stringBufferTail.append(++i).newLine();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		status = Status.SUCCESS;
		return 0;
	}

	@Override
	protected void onFinally() {
		notifyListeners();
	}

	@Override
	public void stop() {
		status = Status.KILLED;
		super.stop();
	}

	@Override
	public ProcessStatus getStatus() {
		processStatus.setStatus(status);
		return processStatus;
	}
}
