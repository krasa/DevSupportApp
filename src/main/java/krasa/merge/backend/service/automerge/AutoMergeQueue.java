package krasa.merge.backend.service.automerge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class AutoMergeQueue {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	Queue<AutoMergeJob> queue = new ConcurrentLinkedQueue<>();

	@Nullable
	public AutoMergeJob getAndRemove(AutoMergeJob request) {
		List<AutoMergeJob> autoMergeJobs = new ArrayList<>();
		for (AutoMergeJob autoMergeJob : queue) {
			if (request.isSameDestination(autoMergeJob)) {
				autoMergeJobs.add(autoMergeJob);
			}
		}
		AutoMergeJob.sort(autoMergeJobs);
		final AutoMergeJob autoMergeJob;
		if (autoMergeJobs.isEmpty()) {
			autoMergeJob = null;
		} else {
			autoMergeJob = autoMergeJobs.get(0);
			queue.remove(autoMergeJob);
		}
		return autoMergeJob;
	}

	public void put(AutoMergeJob job) {
		queue.add(job);
	}

}
