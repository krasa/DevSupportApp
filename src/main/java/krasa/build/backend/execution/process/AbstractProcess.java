package krasa.build.backend.execution.process;

import java.util.ArrayList;
import java.util.List;

import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProcess implements Process {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected ProcessStatus processStatus = new ProcessStatus();
	protected List<ProcessStatusListener> processStatusListeners = new ArrayList<ProcessStatusListener>();
	protected ProcessLog processLog;

	@Override
	public ProcessLog getProcessLog() {
		return processLog;
	}

	public boolean addListener(ProcessStatusListener processStatusListener) {
		return processStatusListeners.add(processStatusListener);
	}

	protected void onFinally() {
		log.info("process complete. " + this.toString());
		notifyListeners();
	}

	@Override
	public void run() {
		onStart();
		try {
			runInternal();
		} catch (Exception e) {
			e.printStackTrace();
			processStatus.setException(e);
			processStatus.setStatus(Status.EXCEPTION);
		} finally {
			try {
				onFinally();
			} catch (Exception e) {
				e.printStackTrace();
				processStatus.setException(e);
				processStatus.setStatus(Status.EXCEPTION);
			}
		}
	}

	protected abstract void runInternal() throws Exception;

	private void onStart() {
		processStatus.setStatus(Status.IN_PROGRESS);
		notifyListeners();
	}

	protected void notifyListeners() {
		for (ProcessStatusListener processStatusListener : processStatusListeners) {
			processStatusListener.onStatusChanged(processStatus);
		}
	}

	@Override
	public void stop() {
		processStatus.setStatus(Status.KILLED);
		onFinally();
	}

	@Override
	public ProcessStatus getStatus() {
		return processStatus;
	}
}
