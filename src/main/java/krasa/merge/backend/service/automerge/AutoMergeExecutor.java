package krasa.merge.backend.service.automerge;

import java.util.*;

import javax.annotation.Nullable;

import krasa.build.backend.config.ExecutorConfig;
import krasa.build.backend.facade.EventService;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.backend.service.MergeService;
import krasa.merge.backend.service.automerge.domain.MergeJob;

import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Service
public class AutoMergeExecutor {

	@Autowired
	MergeJobsHolder runningTasks;

	@Autowired
	@Qualifier(ExecutorConfig.BUILD_EXECUTOR)
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private AutoMergeQueue autoMergeQueue;

	@Autowired
	private MergeService mergeService;

	@Autowired
	EventService eventService;

	public AutoMergeExecutor() {
	}

	public synchronized void schedule(MergeJob mergeJob) {
		String toPath = mergeJob.getToPath();
		if (runningTasks.containsKey(toPath)) {
			autoMergeQueue.put(mergeJob);
		} else {
			AutoMergeProcess autoMergeProcess = new AutoMergeProcess(mergeJob, AutoMergeExecutor.this);
			runningTasks.put(toPath, autoMergeProcess);
			taskExecutor.execute(autoMergeProcess);
		}
	}

	public void jobFinished(AutoMergeProcess autoMergeProcess, Throwable e) {
		if (e != null) {
			e.printStackTrace();
		}
		MergeJob mergeJob1 = autoMergeProcess.getMergeJob();
		runningTasks.remove(mergeJob1.getToPath());
		MergeJob mergeJob = autoMergeQueue.getAndRemove(mergeJob1);
		if (mergeJob != null) {
			schedule(mergeJob);
		}
	}

	public List<MergeJobDto> getRunningMergeJobs() {
		final Collection<AutoMergeProcess> values = runningTasks.getAll();
		final Collection<MergeJob> lastFinished = Collections2.transform(values,
				new Function<AutoMergeProcess, MergeJob>() {

					@Nullable
					@Override
					public MergeJob apply(@Nullable AutoMergeProcess input) {
						return input.getMergeJob();
					}
				});

		return MergeJobDto.translate(lastFinished);
	}

	public void statusUpdated(MergeJob mergeJob) {
		mergeService.update(mergeJob);
		eventService.sendEvent(new MergeEvent());
	}
}
