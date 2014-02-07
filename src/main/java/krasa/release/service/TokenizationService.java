package krasa.release.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import krasa.build.backend.domain.Status;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.facade.Facade;
import krasa.release.domain.TokenizationJob;
import krasa.release.tokenization.TokenizationJobCommand;
import krasa.release.tokenization.TokenizationJobParameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

/**
 * @author Vojtech Krasa
 */
@Service
public class TokenizationService {

	protected GenericDAO<TokenizationJob> tokenizationJobGenericDAO;
	@Value("${tempDir}")
	String tempDir;
	@Autowired
	Facade facade;

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		tokenizationJobGenericDAO = genericDAO.build(TokenizationJob.class);
	}

	@Transactional
	public File tokenizeSynchronously(String branchNamePattern, Integer fromVersion, Integer toVersion, String json) {
		final TokenizationJob tokenizationJob = createJob(branchNamePattern, fromVersion, toVersion, json);

		final TokenizationJobCommand jobCommand = tokenizationJob.prepareCommand(new File(tempDir));
		String logName = jobCommand.getLogName();
		try {
			tokenizationJob.setLogName(logName);
			tokenizationJob.setStatus(Status.RUNNING);
			tokenizationJobGenericDAO.save(tokenizationJob);

			jobCommand.run();

			tokenizationJob.setStatus(Status.SUCCESS);
			tokenizationJob.setEnd(new Date());
			tokenizationJobGenericDAO.save(tokenizationJob);
		} catch (Exception e) {
			tokenizationJob.setStatus(Status.EXCEPTION);
			tokenizationJobGenericDAO.save(tokenizationJob);
			throw new RuntimeException(e);
		}

		return TokenizationFileUtils.getLogFileByName(logName);
	}

	protected TokenizationJob createJob(String branchNamePattern, Integer fromVersion, Integer toVersion, String json) {
		final String svnUrl = getSvnUrl();
		final TokenizationJobParameters jobParameters = new Gson().fromJson(json, TokenizationJobParameters.class);
		jobParameters.generatePlaceholdersReplacements(fromVersion, toVersion);

		final TokenizationJob tokenizationJobCommand = new TokenizationJob(jobParameters, svnUrl, branchNamePattern,
				fromVersion, toVersion);
		tokenizationJobGenericDAO.save(tokenizationJobCommand);
		return tokenizationJobCommand;
	}

	@Transactional
	public List<TokenizationJob> getJobs() {
		return tokenizationJobGenericDAO.findLast(10);
	}

	private String getSvnUrl() {
		Repository defaultRepository = facade.getGlobalSettings().getDefaultRepository();
		return defaultRepository.getUrl();
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
