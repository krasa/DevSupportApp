package krasa.merge.backend.service.automerge;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nullable;

import krasa.merge.backend.service.automerge.domain.MergeJob;

import org.slf4j.*;
import org.springframework.stereotype.Component;

@Component
public class AutoMergeQueue {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	Queue<MergeJob> queue = new ConcurrentLinkedQueue<>();

	@Nullable
	public MergeJob getAndRemove(MergeJob request) {
		List<MergeJob> mergeJobs = new ArrayList<>();
		for (MergeJob mergeJob : queue) {
			if (request.isSameDestination(mergeJob)) {
				mergeJobs.add(mergeJob);
			}
		}
		MergeJob.sort(mergeJobs);
		final MergeJob mergeJob;
		if (mergeJobs.isEmpty()) {
			mergeJob = null;
		} else {
			mergeJob = mergeJobs.get(0);
			queue.remove(mergeJob);
		}
		return mergeJob;
	}

	public void put(MergeJob job) {
		queue.add(job);
	}

}
