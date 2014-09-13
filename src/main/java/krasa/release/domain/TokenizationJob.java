package krasa.release.domain;

import java.io.*;
import java.util.Date;

import javax.persistence.*;

import krasa.build.backend.domain.Status;
import krasa.core.backend.LogNamePrefixes;
import krasa.core.backend.domain.AbstractEntity;
import krasa.release.tokenization.*;

import org.springframework.util.Assert;

@Entity
public class TokenizationJob extends AbstractEntity implements Serializable {

	public static final int LENGTH = 5000;
	@Transient
	private TokenizationJobParameters jobParameters;
	@Column(length = LENGTH)
	private String jobParametersAsString;
	@Column
	private String svnUrl;
	@Column
	@Enumerated(EnumType.STRING)
	public volatile Status status = Status.PENDING;
	@Column
	private String logName;
	@Column
	private String branchNamePattern;
	@Column
	private Date start;
	@Column
	private Date end;
	@Column
	private String caller;

	public TokenizationJob() {
	}

	public TokenizationJob(TokenizationJobParameters jobParameters, String svnUrl, String branchNamePattern,
			String caller) {
		this.jobParameters = jobParameters;
		jobParametersAsString = TokenizationJobParameters.toUglyJson(jobParameters);
		if (jobParametersAsString.length() > LENGTH) {
			throw new IllegalStateException("json is too long " + jobParametersAsString.length());
		}
		this.svnUrl = svnUrl;
		this.branchNamePattern = branchNamePattern;
		start = new Date();
		this.caller = caller;
	}

	public TokenizationJobParameters getJobParameters() {
		if (jobParameters == null && jobParametersAsString != null) {
			jobParameters = TokenizationJobParameters.fromJson(jobParametersAsString);
		}
		return jobParameters;
	}

	public void setJobParameters(TokenizationJobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}

	public String getJobParametersAsString() {
		return jobParametersAsString;
	}

	public void setJobParametersAsString(String jobParametersAsString) {
		this.jobParametersAsString = jobParametersAsString;
	}

	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getBranchNamePattern() {
		return branchNamePattern;
	}

	public void setBranchNamePattern(String branchNamePattern) {
		this.branchNamePattern = branchNamePattern;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public TokenizationJobCommand prepareCommand(File tempDir, boolean commit) {
		Assert.notNull(jobParameters);
		Assert.notNull(svnUrl);
		Assert.notNull(getId());
		Assert.notNull(branchNamePattern);
		TokenizationJobCommand tokenizationJobCommand = new TokenizationJobCommand(getId(), jobParameters, svnUrl,
				getUniqueTempDir(tempDir), branchNamePattern);
		tokenizationJobCommand.setCommit(commit);
		return tokenizationJobCommand;
	}

	private File getUniqueTempDir(File tempDir) {
		int i = 0;
		File file = new File(tempDir.getAbsolutePath(), LogNamePrefixes.BRANCH_TOKENIZER + id);
		while (file.exists()) {
			file = new File(tempDir.getAbsolutePath(), LogNamePrefixes.BRANCH_TOKENIZER + id + "_" + i);
			i++;
		}
		return file;
	}
}
