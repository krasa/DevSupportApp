package krasa.release.tokenization;

import java.util.ArrayList;
import java.util.List;

public class ReplacementDefinition {

	private List<String> includes = new ArrayList<>();
	private List<Replacement> replacements = new ArrayList<>();

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<Replacement> getReplacements() {
		return replacements;
	}

	public void setReplacements(List<Replacement> replacements) {
		this.replacements = replacements;
	}
}
