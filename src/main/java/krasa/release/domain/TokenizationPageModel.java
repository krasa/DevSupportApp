package krasa.release.domain;

import java.io.Serializable;
import java.util.*;

import krasa.release.tokenization.Default;

public class TokenizationPageModel implements Serializable {

	private List<String> branchesPatterns = new ArrayList<String>();
	private String branchNamePatternTemplate;
	private String json;
	private String fromVersion = "9999";
	private String toVersion;
	private String newPortalDb;
	private String newSacDb;
	private String newPitDb;
	private String commitMessage = "##config version";

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public List<String> getBranchesPatterns() {
		return branchesPatterns;
	}

	public void setBranchesPatterns(List<String> branchesPatterns) {
		this.branchesPatterns = branchesPatterns;
	}

	public String getBranchNamePatternTemplate() {
		return branchNamePatternTemplate;
	}

	public void setBranchNamePatternTemplate(String branchNamePatternTemplate) {
		this.branchNamePatternTemplate = branchNamePatternTemplate;
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
		if (branchNamePatternTemplate == null || branchNamePatternTemplate.startsWith(".*_")) {
			branchNamePatternTemplate = ".*_" + toVersion;
		}
		String dbVersion;
		try {
			if (Integer.valueOf(toVersion) != 9999) {
				dbVersion = String.valueOf((Integer.valueOf(toVersion) / 10) * 10);
			} else {
				dbVersion = toVersion;
			}
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
		branchNamePatternTemplate = currentProfile.branchNamePatternTemplate;
		json = currentProfile.json;
		fromVersion = currentProfile.fromVersion;
		toVersion = currentProfile.toVersion;
		newPortalDb = currentProfile.newPortalDb;
		newSacDb = currentProfile.newSacDb;
		newPitDb = currentProfile.newPitDb;
	}
}
