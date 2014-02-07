package krasa.release.tokenization;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import krasa.core.backend.domain.AbstractEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TokenizationJobParameters extends AbstractEntity {
	private Map<String, String> placeholderReplace = new TreeMap<>();
	private List<ReplacementDefinition> replacementDefinitions = new ArrayList<>();

	public TokenizationJobParameters() {
	}

	public TokenizationJobParameters(List<ReplacementDefinition> replacementDefinitions,
			Map<String, String> placeholderReplace) {
		this.replacementDefinitions = replacementDefinitions;
		this.placeholderReplace = placeholderReplace;
	}

	protected static String toPomVersion(String fromVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(substring(fromVersion, 0, 2));
		sb.append(".");
		sb.append(substring(fromVersion, 2, 3));
		sb.append(".");
		sb.append(substring(fromVersion, 3, 4));

		String substring = substring(fromVersion, 4, 5);
		if (substring.length() > 0 && !substring.equals("0")) {
			sb.append(".");
			sb.append(substring);
		}

		if ("9999".equals(fromVersion)) {
			sb.append("-SNAPSHOT");
		}
		return sb.toString();
	}

	public static String toJson(TokenizationJobParameters tokenizationJobParameters) {
		return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(tokenizationJobParameters);
	}

	public static String toUglyJson(TokenizationJobParameters tokenizationJobParameters) {
		return new GsonBuilder().disableHtmlEscaping().create().toJson(tokenizationJobParameters);
	}

	public static TokenizationJobParameters fromJson(String s) {
		return new Gson().fromJson(s, TokenizationJobParameters.class);
	}

	public List<ReplacementDefinition> getReplacementDefinitions() {
		return replacementDefinitions;
	}

	public void setReplacementDefinitions(List<ReplacementDefinition> replacementDefinitions) {
		this.replacementDefinitions = replacementDefinitions;
	}

	public Map<String, String> getPlaceholderReplace() {
		return placeholderReplace;
	}

	public void setPlaceholderReplace(Map<String, String> placeholderReplace) {
		this.placeholderReplace = placeholderReplace;
	}

	public void generatePlaceholdersReplacements(Integer fromVersion, Integer toVersion) {
		String fromVersionString = String.valueOf(fromVersion);
		String toVersionString = String.valueOf(toVersion);

		placeholderReplace.put("old.build.version", fromVersionString);
		placeholderReplace.put("old.db.version", fromVersionString);
		placeholderReplace.put("old.pom.version", toPomVersion(fromVersionString));

		placeholderReplace.put("new.build.version", toVersionString);
		placeholderReplace.put("new.db.version", toVersionString);
		placeholderReplace.put("new.pom.version", toPomVersion(toVersionString));
	}

}
