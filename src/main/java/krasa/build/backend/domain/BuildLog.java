package krasa.build.backend.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import krasa.core.backend.domain.AbstractEntity;

/**
 * @author Vojtech Krasa
 */
@Entity
public class BuildLog extends AbstractEntity {
	@OneToOne(optional = false)
	private BuildJob buildJob;
	@Column(name = "logContent", columnDefinition = "LONGVARCHAR")
	private String logContent;

	public BuildJob getBuildJob() {
		return buildJob;
	}

	public void setBuildJob(BuildJob buildJob) {
		this.buildJob = buildJob;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
}
