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
	TokenizationService tokenizationService;
	@Value("${tempDir}")
	String tempDir;
	@Value("${tokenization.commit}")
	Boolean commit;
	@Autowired
	@Qualifier(ExecutorConfig.TOKENIZATION_EXECUTOR)
	private ThreadPoolTaskExecutor taskExecutor;

	public synchronized void schedule(TokenizationJob autoMergeJob) {
		TokenizationProcess tokenizationProcess = new TokenizationProcess(autoMergeJob, this);
		taskExecutor.execute(tokenizationProcess);
		runningTasks.put(tokenizationProcess);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void jobFinished(TokenizationProcess process, Throwable e) {
		TokenizationJob tokenizationJob = process.getJob();
		if (e != null) {
			e.printStackTrace();
		}
		runningTasks.remove(process);
		tokenizationService.update(tokenizationJob);
	}

	public Collection<TokenizationProcess> getProcesses() {
		Collection<TokenizationProcess> values = runningTasks.getAll();
		return values;
	}

	public String getTempDir() {
		return tempDir;
	}

	public Boolean getCommit() {
		return commit;
	}
}
