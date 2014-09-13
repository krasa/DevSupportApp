package krasa.release.tokenization;

import java.util.*;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.*;

import com.google.gson.*;

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

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
