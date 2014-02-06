package krasa.release.tokenization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchTokenReplacementJobParameters {

	private List<ReplacementDefinition> replacementDefinitions = new ArrayList<>();
	private Map<String, String> placeholderReplace = new HashMap<>();

	public BranchTokenReplacementJobParameters() {
	}

	public BranchTokenReplacementJobParameters(List<ReplacementDefinition> replacementDefinitions,
			Map<String, String> placeholderReplace) {
		this.replacementDefinitions = replacementDefinitions;
		this.placeholderReplace = placeholderReplace;
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
}
