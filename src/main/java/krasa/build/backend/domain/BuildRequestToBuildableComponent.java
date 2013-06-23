package krasa.build.backend.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import krasa.core.backend.domain.AbstractEntity;

@Entity
public class BuildRequestToBuildableComponent extends AbstractEntity {
	@ManyToOne(optional = false)
	private BuildableComponent buildableComponent;
	@ManyToOne(optional = false)
	private BuildRequest buildRequest;

	public BuildRequestToBuildableComponent() {
	}

	public BuildRequestToBuildableComponent(BuildableComponent buildableComponent, BuildRequest buildRequest) {
		this.buildableComponent = buildableComponent;
		buildableComponent.setLastBuildRequestToBuildableComponent(this);
		this.buildRequest = buildRequest;
	}

	public BuildableComponent getBuildableComponent() {
		return buildableComponent;
	}

	public void setBuildableComponent(BuildableComponent buildableComponent) {
		this.buildableComponent = buildableComponent;
	}

	public BuildRequest getBuildRequest() {
		return buildRequest;
	}

	public void setBuildRequest(BuildRequest buildRequest) {
		this.buildRequest = buildRequest;
	}

	public BuildJob getBuildJob() {
		return buildRequest.getBuildJob();
	}
}
