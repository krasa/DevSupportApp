package krasa.build.backend.execution.process;

import java.util.*;

import krasa.build.backend.domain.*;
import krasa.build.backend.execution.ProcessStatus;
import krasa.core.backend.utils.MdcUtils;

import org.slf4j.*;

import com.google.common.base.Objects;

public abstract class AbstractProcess implements Process {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected ProcessStatus processStatus = new ProcessStatus(Status.PENDING);
	protected List<ProcessStatusListener> processStatusListeners = new ArrayList<>();
	protected ProcessLog processLog;
	private BuildJob buildJob;

	public AbstractProcess(BuildJob buildJob) {
		this.buildJob = buildJob;
	}

	@Override
	public ProcessLog getProcessLog() {
		return processLog;
	}

	public boolean addListener(ProcessStatusListener processStatusListener) {
		return processStatusListeners.add(processStatusListener);
	}

	protected void releaseResources() {
		log.info("process complete. " + this.toString());
	}

	@Override
	public void run() {
		MdcUtils.putLogName(buildJob.getLogFileName());
		onStart();
		try {
			runInternal();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("process failed", e);
			processStatus.setException(e);
			processStatus.setStatus(Status.EXCEPTION);
		} finally {
			try {
				releaseResources();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("process #finally failed", e);
				processStatus.setException(e);
				processStatus.setStatus(Status.EXCEPTION);
			}
			notifyListeners();
		}
	}

	protected abstract void runInternal() throws Exception;

	private void onStart() {
		processStatus.setStatus(Status.RUNNING);
		notifyListeners();
	}

	protected void notifyListeners() {
		for (ProcessStatusListener processStatusListener : processStatusListeners) {
			processStatusListener.onStatusChanged(processStatus);
		}
	}

	@Override
	public void stop(String reason) {
		processLog.newLine();
		processLog.append(reason);
		processStatus.setStatus(Status.KILLED);
		processStatus.setException(new RuntimeException());
		releaseResources();
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
