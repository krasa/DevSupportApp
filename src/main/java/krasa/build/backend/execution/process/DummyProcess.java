package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.List;

import krasa.build.backend.domain.Status;

import com.google.common.base.Objects;

public class DummyProcess extends JschSshBuildProcess {

	public DummyProcess(ProcessLog stringBufferTail, List<String> command) {
		super(stringBufferTail, command);
	}

	@Override
	protected int doWork() throws IOException {
		int i = 0;
		while (i < 20 && processStatus.getStatus() != Status.KILLED) {
			processLog.append(String.valueOf(++i)).newLine();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (processStatus.getStatus() == Status.RUNNING) {
			processStatus.setStatus(Status.SUCCESS);
		}
		return 0;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", processStatus).toString();
	}
}
