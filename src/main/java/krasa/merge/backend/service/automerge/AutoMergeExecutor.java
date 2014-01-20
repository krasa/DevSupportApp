package krasa.merge.backend.service.automerge;

import java.util.Collection;
import java.util.List;

import krasa.build.backend.config.ExecutorConfig;
import krasa.merge.backend.dto.MergeJobDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class AutoMergeExecutor {

	@Autowired
	MergeJobsHolder runningTasks;

	@Autowired
	@Qualifier(ExecutorConfig.BUILD_EXECUTOR)
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private AutoMergeQueue autoMergeQueue;

	public AutoMergeExecutor() {
	}

	public synchronized void schedule(AutoMergeJob autoMergeJob) {
		String toPath = autoMergeJob.getToPath();
		if (runningTasks.containsKey(toPath)) {
			autoMergeQueue.put(autoMergeJob);
		} else {
			AutoMergeProcess autoMergeProcess = new AutoMergeProcess(autoMergeJob, AutoMergeExecutor.this);
			runningTasks.put(toPath, autoMergeProcess);
			taskExecutor.execute(autoMergeProcess);
		}
	}

	public void jobFinished(AutoMergeProcess autoMergeProcess, Throwable e) {
		if (e != null) {
			e.printStackTrace();
		}
		runningTasks.remove(autoMergeProcess.getAutoMergeJob().getToPath());
		AutoMergeJob autoMergeJob = autoMergeQueue.getAndRemove(autoMergeProcess.getAutoMergeJob());
		if (autoMergeJob != null) {
			schedule(autoMergeJob);
		}

	}

	public List<MergeJobDto> getRunningMergeJobs() {
		final Collection<AutoMergeProcess> values = runningTasks.getAll();
		return MergeJobDto.translate(values);
	}
}
