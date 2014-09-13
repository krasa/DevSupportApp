package krasa.release.domain;

import java.io.Serializable;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;
import krasa.release.tokenization.Default;

@Entity
public class TokenizationPageModel extends AbstractEntity implements Serializable {
	@Column
	private String branchNamePattern;
	@Column(length = TokenizationJob.LENGTH)
	private String json;
	@Column
	private String fromVersion = "9999";
	@Column
	private String toVersion;
	@Column
	private String newPortalDb;
	@Column
	private String newSacDb;
	@Column
	private String newPitDb;

	public String getBranchNamePattern() {
		return branchNamePattern;
	}

	public void setBranchNamePattern(String branchNamePattern) {
		this.branchNamePattern = branchNamePattern;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
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

	public String getNewPortalDb() {
		return newPortalDb;
	}

	public void setNewPortalDb(String newPortalDb) {
		this.newPortalDb = newPortalDb;
	}

	public String getNewSacDb() {
		return newSacDb;
	}

	public void setNewSacDb(String newSacDb) {
		this.newSacDb = newSacDb;
	}

	public String getNewPitDb() {
		return newPitDb;
	}

	public void setNewPitDb(String newPitDb) {
		this.newPitDb = newPitDb;
	}

	public void updateFields() {
		if (branchNamePattern == null || branchNamePattern.startsWith(".*_")) {
			branchNamePattern = ".*_" + toVersion;
		}
		String dbVersion;
		try {
			dbVersion = String.valueOf((Integer.valueOf(toVersion) / 10) * 10);
		} catch (NumberFormatException e) {
			dbVersion = toVersion;
		}
		newPortalDb = dbVersion;
		newSacDb = dbVersion;
		newSacDb = dbVersion;
		newPitDb = dbVersion;
	}

	public void regenerateJson() {
		setJson(Default.generateJson(fromVersion, toVersion, newPortalDb, newSacDb, newPitDb));
	}

	public void importFrom(TokenizationPageModel currentProfile) {
		branchNamePattern = currentProfile.branchNamePattern;
		json = currentProfile.json;
		fromVersion = currentProfile.fromVersion;
		toVersion = currentProfile.toVersion;
		newPortalDb = currentProfile.newPortalDb;
		newSacDb = currentProfile.newSacDb;
		newPitDb = currentProfile.newPitDb;
	}
}
