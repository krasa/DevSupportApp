package krasa.release.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import krasa.build.backend.domain.Status;
import krasa.core.backend.domain.AbstractEntity;
import krasa.release.tokenization.TokenizationJobCommand;
import krasa.release.tokenization.TokenizationJobParameters;

@Entity
public class TokenizationJob extends AbstractEntity implements Serializable {
	@Transient
	private TokenizationJobParameters jobParameters;
	@Column(length = 1000)
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
	private String fromVersion;
	@Column
	private String toVersion;
	@Column
	private Date start;
	@Column
	private Date end;

	public TokenizationJob() {
	}

	public TokenizationJob(TokenizationJobParameters jobParameters, String svnUrl, String branchNamePattern,
			Integer fromVersion, Integer toVersion) {
		this.jobParameters = jobParameters;
		this.fromVersion = String.valueOf(fromVersion);
		this.toVersion = String.valueOf(toVersion);
		jobParametersAsString = TokenizationJobParameters.toUglyJson(jobParameters);
		this.svnUrl = svnUrl;
		this.branchNamePattern = branchNamePattern;
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

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
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

	public TokenizationJobCommand prepareCommand(File file) {
		return new TokenizationJobCommand(jobParameters, svnUrl, getUniqueTempDir(file), branchNamePattern);
	}

	private File getUniqueTempDir(File tempDir) {
		int i = 0;
		File file = new File(tempDir.getAbsolutePath(), String.valueOf(id));
		while (file.exists()) {
			file = new File(tempDir.getAbsolutePath(), id + "_" + i);
			i++;
		}
		file.mkdirs();
		return file;
	}
}
