package krasa.merge.backend.service.automerge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.istack.internal.Nullable;

@Component
public class AutoMergeQueue {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	Queue<AutoMergeJob> queue = new ConcurrentLinkedQueue<>();

	@Nullable
	public AutoMergeJob getAndRemove(AutoMergeJob request) {
		List<AutoMergeJob> autoMergeJobs = new ArrayList<>();
		for (AutoMergeJob autoMergeJob : queue) {
			if (autoMergeJob.getToPath().equals(request.getToPath())) {
				autoMergeJobs.add(autoMergeJob);
			}
		}
		sort(autoMergeJobs);
		final AutoMergeJob autoMergeJob;
		if (autoMergeJobs.isEmpty()) {
			autoMergeJob = null;
		} else {
			autoMergeJob = autoMergeJobs.get(0);
			queue.remove(autoMergeJob);
		}
		return autoMergeJob;
	}

	protected void sort(List<AutoMergeJob> autoMergeJobs) {
		Collections.sort(autoMergeJobs, new Comparator<AutoMergeJob>() {

			@Override
			public int compare(AutoMergeJob o1, AutoMergeJob o2) {
				Long revision = o1.getRevision();
				return revision.compareTo(o2.getRevision());
			}
		});
	}

	public void put(AutoMergeJob job) {
		queue.add(job);
	}

}
