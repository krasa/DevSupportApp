package krasa.merge.backend.service.automerge;

import javax.validation.constraints.NotNull;

import krasa.build.backend.domain.Status;
import krasa.merge.backend.service.automerge.domain.MergeJob;

import org.slf4j.*;

public class AutoMergeProcess implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private MergeJob mergeJob;
	private AutoMergeService autoMergeService;

	public AutoMergeProcess(@NotNull MergeJob mergeJob, AutoMergeService autoMergeService) {
		this.mergeJob = mergeJob;
		this.autoMergeService = autoMergeService;
	}

	public MergeJob getMergeJob() {
		return mergeJob;
	}

	@Override
	public void run() {
		try {
			updateStatus(Status.RUNNING);
			mergeJob.merge();
			updateStatus(Status.SUCCESS);
			autoMergeService.jobFinished(this, null);
		} catch (Throwable e) {
			updateStatus(Status.EXCEPTION);
			autoMergeService.jobFinished(this, e);
		}
	}

	private void updateStatus(Status running) {
		mergeJob.setStatus(running);
		autoMergeService.statusUpdated(mergeJob);
	}

}
