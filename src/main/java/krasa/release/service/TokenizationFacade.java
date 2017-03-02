package krasa.release.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import krasa.build.backend.domain.Status;
import krasa.build.backend.facade.EventService;
import krasa.build.backend.facade.UsernameException;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.frontend.pages.FileSystemLogUtils;
import krasa.core.frontend.web.CookieUtils;
import krasa.release.domain.TokenizationJob;
import krasa.release.domain.TokenizationPageModel;
import krasa.release.tokenization.TokenizationJobParameters;
import krasa.release.tokenization.TokenizationJobProcess;
import krasa.release.tokenization.TokenizationResult;
import krasa.svn.backend.domain.Repository;
import krasa.svn.backend.facade.SvnFacade;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
public class TokenizationFacade {

	private static final Logger log = LoggerFactory.getLogger(TokenizationFacade.class);

	protected GenericDAO<TokenizationJob> tokenizationJobGenericDAO;
	@Value("${tempDir}")
	String tempDir;
	@Value("${tokenization.commit}")
	Boolean commit;
	@Autowired
	SvnFacade facade;
	@Autowired
	TokenizationExecutor tokenizationExecutor;
	@Autowired
	EventService eventService;

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		tokenizationJobGenericDAO = genericDAO.build(TokenizationJob.class);
	}

	public TokenizationResult tokenizeSynchronously(TokenizationPageModel json) throws UsernameException {
		TokenizationJob tokenizationJob = createJob(json);

		TokenizationJobProcess jobCommand = tokenizationJob.prepareProcess(new File(tempDir), commit);
		String logName = jobCommand.getLogName();
		try {
			tokenizationJob.setLogName(logName);
			tokenizationJob.setStatus(Status.RUNNING);
			save(tokenizationJob);

			jobCommand.run();

			tokenizationJob.setStatus(Status.SUCCESS);
			tokenizationJob.setEnd(new Date());
			save(tokenizationJob);
		} catch (Throwable e) {
			log.error(String.valueOf(e.getMessage()), e);
			tokenizationJob.setStatus(Status.EXCEPTION);
			tokenizationJob.setEnd(new Date());
			save(tokenizationJob);
		}

		File logFileByName = FileSystemLogUtils.getLogFileByName(logName);
		return new TokenizationResult(logFileByName, tokenizationJob.getStatus());
	}

	private TokenizationJob save(TokenizationJob tokenizationJob) {
		return tokenizationJobGenericDAO.save(tokenizationJob);
	}

	public File tokenizeAsync(TokenizationPageModel json) throws UsernameException {
		TokenizationJob tokenizationJob = createJob(json);

		TokenizationJobProcess jobCommand = tokenizationJob.prepareProcess(new File(tempDir), commit);
		String logName = jobCommand.getLogName();
		tokenizationJob.setLogName(logName);
		tokenizationJob.setStatus(Status.PENDING);
		save(tokenizationJob);

		tokenizationExecutor.schedule(tokenizationJob);

		return FileSystemLogUtils.getLogFileByName(logName);
	}

	protected TokenizationJob createJob(TokenizationPageModel json) throws UsernameException {
		String svnUrl = getSvnUrl();
		TokenizationJobParameters jobParameters = new Gson().fromJson(json.getJson(), TokenizationJobParameters.class);
		TokenizationJob tokenizationJobCommand = new TokenizationJob(jobParameters, svnUrl, json.getBranchesPatterns(),
				CookieUtils.getValidUsername(), json.getCommitMessage());
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
