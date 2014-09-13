package krasa.release.tokenization;

import java.util.*;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.*;

public class ReplacementDefinition extends AbstractEntity {

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
