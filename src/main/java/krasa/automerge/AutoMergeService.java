package krasa.automerge;

import java.util.*;

import javax.annotation.Nullable;

import krasa.automerge.domain.MergeJob;
import krasa.build.backend.facade.EventService;
import krasa.svn.backend.dto.MergeJobDto;
import krasa.svn.backend.service.MergeFacade;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Service
public class AutoMergeService {
	private static final Logger log = LoggerFactory.getLogger(AutoMergeService.class);
	@Autowired
	MergeJobsHolder runningTasks;

	@Autowired
	AutoMergeExecutor autoMergeExecutor;

	@Autowired
	private AutoMergeQueue autoMergeQueue;

	@Autowired
	private MergeFacade mergeFacade;

	@Autowired
	EventService eventService;

	public AutoMergeService() {
	}

	public synchronized void schedule(MergeJob mergeJob) {
		String toPath = mergeJob.getToPath();
		if (runningTasks.containsKey(toPath)) {
			autoMergeQueue.put(mergeJob);
		} else {
			AutoMergeCommand autoMergeCommand = new AutoMergeCommand(mergeJob, AutoMergeService.this);
			runningTasks.put(toPath, autoMergeCommand);
			autoMergeExecutor.executeAutoMerge(autoMergeCommand);
		}
	}

	public void jobFinished(AutoMergeCommand autoMergeCommand, Throwable e) {
		if (e != null) {
			log.error("", e);
		}
		MergeJob mergeJob1 = autoMergeCommand.getMergeJob();
		runningTasks.remove(mergeJob1.getToPath());
		MergeJob mergeJob = autoMergeQueue.getAndRemove(mergeJob1);
		if (mergeJob != null) {
			schedule(mergeJob);
		}
	}

	public List<MergeJobDto> getRunningMergeJobs() {
		Collection<AutoMergeCommand> values = runningTasks.getAll();
		Collection<MergeJob> lastFinished = Collections2.transform(values, new Function<AutoMergeCommand, MergeJob>() {

			@Nullable
			@Override
			public MergeJob apply(@Nullable AutoMergeCommand input) {
				return input.getMergeJob();
			}
		});

		return MergeJobDto.translate(lastFinished);
	}

	public void statusUpdated(MergeJob mergeJob) {
		mergeFacade.update(mergeJob);
		sendMergeEvent();
	}

	private void sendMergeEvent() {
		log.debug("sending MergeEvent");
		eventService.sendEvent(new MergeEvent());
	}
}
