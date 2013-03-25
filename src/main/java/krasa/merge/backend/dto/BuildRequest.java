package krasa.merge.backend.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krasa.build.backend.domain.BuildableComponent;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BuildRequest implements Serializable {
	private String environmentName;
	private List<String> components;
	private List<BuildableComponent> buildableComponent;

	public BuildRequest(List<String> components, final String environmentName) {
		this.environmentName = environmentName;
		this.components = components;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public List<String> getComponents() {
		return components;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public void setComponents(List<String> components) {
		this.components = components;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BuildRequest{");
		sb.append("environment=").append(environmentName);
		sb.append(", components=").append(components);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, "componentBuild");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "componentBuild");
	}

	public void validate() {
		if (environmentName == null) {
			throw new IllegalArgumentException("null environment");
		}
		if (components.isEmpty()) {
			throw new IllegalArgumentException("empty component");
		}
	}

	public Set<String> getComponentsAsSet() {
		return new HashSet<String>(getComponents());
	}

	public void setBuildableComponent(List<BuildableComponent> buildableComponent) {
		this.buildableComponent = buildableComponent;
	}

	public List<BuildableComponent> getBuildableComponent() {
		return buildableComponent;
	}
}
