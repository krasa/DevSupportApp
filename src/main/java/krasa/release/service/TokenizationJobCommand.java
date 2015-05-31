package krasa.release.service;

import java.io.File;
import java.util.Date;

import javax.validation.constraints.NotNull;

import krasa.build.backend.domain.Status;
import krasa.release.domain.TokenizationJob;
import krasa.release.tokenization.TokenizationJobProcess;

import org.slf4j.*;

public class TokenizationJobCommand implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private TokenizationJob job;
	private TokenizationExecutor executor;

	public TokenizationJobCommand(@NotNull TokenizationJob job, TokenizationExecutor autoMergeExecutor) {
		this.job = job;
		this.executor = autoMergeExecutor;
	}

	public TokenizationJob getJob() {
		return job;
	}

	@Override
	public void run() {
		Throwable e1 = null;
		try {
			TokenizationJobProcess tokenizationJobProcess = job.prepareProcess(new File(executor.getTempDir()),
					executor.getCommit());
			tokenizationJobProcess.run();
			job.setStatus(Status.SUCCESS);
			job.setEnd(new Date());
		} catch (Throwable e) {
			e1 = e;
			log.error(String.valueOf(e1.getMessage()), e1);
			job.setStatus(Status.EXCEPTION);
			job.setEnd(new Date());
		}
		executor.jobFinished(this, e1);
	}

}
