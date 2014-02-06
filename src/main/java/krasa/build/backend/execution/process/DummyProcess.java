package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.List;

import krasa.build.backend.domain.Status;
import krasa.core.backend.utils.MdcUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

public class DummyProcess extends JschSshBuildProcess {
	protected static final Logger log = LoggerFactory.getLogger(DummyProcess.class);

	public DummyProcess(ProcessLog stringBufferTail, List<String> command) {
		super(stringBufferTail, command);
	}

	@Override
	protected int doWork() throws IOException {
		MdcUtils.putLogName("DummyProcess" + System.currentTimeMillis());
		int i = 0;
		Integer integer = Integer.valueOf(RandomStringUtils.randomNumeric(3));
		while (i < integer && processStatus.getStatus() != Status.KILLED) {
			log.info(String.valueOf(++i));
			processLog.append(String.valueOf(++i)).newLine();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (processStatus.getStatus() == Status.RUNNING) {
			processStatus.setStatus(Status.SUCCESS);
		}
		MdcUtils.removeLogName();
		return 0;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", processStatus).toString();
	}
}
