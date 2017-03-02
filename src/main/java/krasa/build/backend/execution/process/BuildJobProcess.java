package krasa.build.backend.execution.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.core.backend.utils.MdcUtils;

public abstract class BuildJobProcess {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected ProcessStatus processStatus = new ProcessStatus(Status.PENDING);
	protected List<ProcessStatusListener> processStatusListeners = new ArrayList<>();
	protected BuildJob buildJob;

	public BuildJobProcess(BuildJob buildJob) {
		this.buildJob = buildJob;
	}

	public boolean addListener(ProcessStatusListener processStatusListener) {
		return processStatusListeners.add(processStatusListener);
	}

	protected void releaseResources() {
		log.info("process complete. " + this.toString());
	}

	public void execute(ReentrantLock reentrantLock, Semaphore semaphore) {
		try {
			reentrantLock.lock();
			semaphore.acquire();

			MdcUtils.putLogName(buildJob.getLogFileName());
			onStart();
			runInternal();
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("process failed", e);
			processStatus.setException(e);
			processStatus.setStatus(Status.EXCEPTION);
		} finally {
			semaphore.release();
			reentrantLock.unlock();
			try {
				releaseResources();
			} catch (Throwable e) {
				e.printStackTrace();
				log.error("process #finally failed", e);
				processStatus.setException(e);
				processStatus.setStatus(Status.EXCEPTION);
			}
			try {
				notifyListeners();
			} finally {
				MdcUtils.removeLogName();
			}
		}
	}

	protected abstract void runInternal() throws Exception;

	private void onStart() {
		processStatus.setStatus(Status.RUNNING);
		notifyListeners();
	}

	protected void notifyListeners() {
		for (ProcessStatusListener processStatusListener : processStatusListeners) {
			processStatusListener.onStatusChanged(buildJob, processStatus);
		}
	}

	public void stop(String reason) {
		try {
			MdcUtils.putLogName(buildJob.getLogFileName());
			log.info("process stopped, {}", reason);
			processStatus.setStatus(Status.KILLED);
			processStatus.setException(new RuntimeException());
			releaseResources();
			notifyListeners();
		} finally {
			MdcUtils.removeLogName();
		}
	}

	public ProcessStatus getStatus() {
		return processStatus;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("status", processStatus).toString();
	}
}
