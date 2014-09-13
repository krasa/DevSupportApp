package krasa.release.service;

import java.io.File;
import java.util.*;

import krasa.build.backend.domain.Status;
import krasa.build.backend.facade.EventService;
import krasa.core.backend.RemoteHostUtils;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.facade.Facade;
import krasa.release.domain.TokenizationJob;
import krasa.release.tokenization.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
public class TokenizationService {

	private static final Logger log = LoggerFactory.getLogger(TokenizationService.class);

	protected GenericDAO<TokenizationJob> tokenizationJobGenericDAO;
	@Value("${tempDir}")
	String tempDir;
	@Value("${tokenization.commit}")
	Boolean commit;
	@Autowired
	Facade facade;
	@Autowired
	TokenizationExecutor tokenizationExecutor;
	@Autowired
	EventService eventService;

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		tokenizationJobGenericDAO = genericDAO.build(TokenizationJob.class);
	}

	public TokenizationResult tokenizeSynchronously(String branchNamePattern, String json) {
		final TokenizationJob tokenizationJob = createJob(branchNamePattern, json);

		final TokenizationJobCommand jobCommand = tokenizationJob.prepareCommand(new File(tempDir), commit);
		String logName = jobCommand.getLogName();
		try {
			tokenizationJob.setLogName(logName);
			tokenizationJob.setStatus(Status.RUNNING);
			save(tokenizationJob);

			jobCommand.run();

			tokenizationJob.setStatus(Status.SUCCESS);
			tokenizationJob.setEnd(new Date());
			save(tokenizationJob);
		} catch (Exception e) {
			log.error(String.valueOf(e.getMessage()), e);
			tokenizationJob.setStatus(Status.EXCEPTION);
			tokenizationJob.setEnd(new Date());
			save(tokenizationJob);
		}

		File logFileByName = TokenizationFileUtils.getLogFileByName(logName);
		return new TokenizationResult(logFileByName, tokenizationJob.getStatus());
	}

	private TokenizationJob save(TokenizationJob tokenizationJob) {
		return tokenizationJobGenericDAO.save(tokenizationJob);
	}

	public File tokenizeAsync(String branchNamePattern, String json) {
		final TokenizationJob tokenizationJob = createJob(branchNamePattern, json);

		final TokenizationJobCommand jobCommand = tokenizationJob.prepareCommand(new File(tempDir), commit);
		String logName = jobCommand.getLogName();
		tokenizationJob.setLogName(logName);
		tokenizationJob.setStatus(Status.PENDING);
		save(tokenizationJob);

		tokenizationExecutor.schedule(tokenizationJob);

		return TokenizationFileUtils.getLogFileByName(logName);
	}

	protected TokenizationJob createJob(String branchNamePattern, String json) {
		final String svnUrl = getSvnUrl();
		final TokenizationJobParameters jobParameters = new Gson().fromJson(json, TokenizationJobParameters.class);
		final TokenizationJob tokenizationJobCommand = new TokenizationJob(jobParameters, svnUrl,
				branchNamePattern.toUpperCase(), RemoteHostUtils.getRemoteHost());
		save(tokenizationJobCommand);
		return tokenizationJobCommand;
	}

	public List<TokenizationJob> getJobs() {
		return tokenizationJobGenericDAO.findLast(10);
	}

	private String getSvnUrl() {
		Repository defaultRepository = facade.getGlobalSettings().getDefaultRepository();
		return defaultRepository.getUrl();
	}

	public void update(TokenizationJob tokenizationJob) {
		save(tokenizationJob);
		eventService.sendEvent(new TokenizationEvent());
	}

	// protected TokenizationJobCommand prepareJob2(String branchName, Integer fromVersion, Integer toVersion, String
	// json) {
	// Repository defaultRepository = facade.getGlobalSettings().getDefaultRepository();
	// final String svnUrl = defaultRepository.getUrl();
	// final TokenizationJobParameters jobParameters = new Gson().fromJson(json, TokenizationJobParameters.class);
	// jobParameters.generatePlaceholdersReplacements(fromVersion, toVersion);
	//
	// return new TokenizationJobCommand(jobParameters, svnUrl, new File(tempDir), branchName);
	// }

}
