package krasa.build.backend.dto;

import java.util.List;

import krasa.build.backend.domain.Environment;

public class BuildRequest {
	private Environment environment;
	private List<String> components;

	public BuildRequest(Environment environment, List<String> components) {
		this.environment = environment;
		this.components = components;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public List<String> getComponents() {
		return components;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BuildRequest{");
		sb.append("environment=").append(environment);
		sb.append(", components=").append(components);
		sb.append('}');
		return sb.toString();
	}
}
