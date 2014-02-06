package krasa.release;

import java.io.File;

import krasa.merge.backend.facade.Facade;
import krasa.release.tokenization.BranchTokenReplacementJobParameters;
import krasa.release.tokenization.BranchesTokenReplacementJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

/**
 * @author Vojtech Krasa
 */
@Service
public class TokenizationService {
	@Value("${tempDir}")
	String tempDir;
	@Autowired
	Facade facade;

	public String tokenize(String branchNameSuffix, String json) {
		final String svnUrl = facade.getGlobalSettings().getDefaultRepository().getUrl();
		final BranchTokenReplacementJobParameters branchTokenReplacementJobParameters = new Gson().fromJson(json,
				BranchTokenReplacementJobParameters.class);
		final BranchesTokenReplacementJob branchesTokenReplacementJob = new BranchesTokenReplacementJob(
				branchTokenReplacementJobParameters, svnUrl, new File(tempDir), branchNameSuffix);
		final String logName = branchesTokenReplacementJob.getLogName();
		return logName;
	}
}
