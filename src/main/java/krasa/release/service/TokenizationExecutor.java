package krasa.release.service;

import java.util.Collection;

import krasa.build.backend.config.ExecutorConfig;
import krasa.core.backend.config.MainConfig;
import krasa.release.domain.TokenizationJob;

import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenizationExecutor {

	@Autowired
	TokenizationJobsHolder runningTasks;
	@Autowired
	TokenizationFacade tokenizationFacade;
	@Value("${tempDir}")
	String tempDir;
	@Value("${tokenization.commit}")
	Boolean commit;
	@Autowired
	@Qualifier(ExecutorConfig.TOKENIZATION_EXECUTOR)
	private ThreadPoolTaskExecutor taskExecutor;

	public synchronized void schedule(TokenizationJob autoMergeJob) {
		TokenizationJobCommand tokenizationJobCommand = new TokenizationJobCommand(autoMergeJob, this);
		taskExecutor.execute(tokenizationJobCommand);
		runningTasks.put(tokenizationJobCommand);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void jobFinished(TokenizationJobCommand process, Throwable e) {
		TokenizationJob tokenizationJob = process.getJob();
		if (e != null) {
			e.printStackTrace();
		}
		runningTasks.remove(process);
		tokenizationFacade.update(tokenizationJob);
	}

	public Collection<TokenizationJobCommand> getProcesses() {
		Collection<TokenizationJobCommand> values = runningTasks.getAll();
		return values;
	}

	public String getTempDir() {
		return tempDir;
	}

	public Boolean getCommit() {
		return commit;
	}
}
