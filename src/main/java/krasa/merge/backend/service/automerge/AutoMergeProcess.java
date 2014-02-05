package krasa.merge.backend.service.automerge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AutoMergeProcess implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private AutoMergeJob autoMergeJob;
	private AutoMergeExecutor autoMergeExecutor;

	public AutoMergeProcess(@NotNull AutoMergeJob autoMergeJob, AutoMergeExecutor autoMergeExecutor) {
		this.autoMergeJob = autoMergeJob;
		this.autoMergeExecutor = autoMergeExecutor;
	}

	public AutoMergeJob getAutoMergeJob() {
		return autoMergeJob;
	}

	@Override
	public void run() {
		try {
			autoMergeJob.merge();
			autoMergeExecutor.jobFinished(this, null);
		} catch (Throwable e) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(out));
			final String str = out.toString();
			autoMergeJob.append(str);
			autoMergeExecutor.jobFinished(this, e);
		}
	}

}
