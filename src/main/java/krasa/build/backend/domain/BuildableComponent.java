package krasa.build.backend.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.ObjectUtils;

@Entity
public class BuildableComponent extends AbstractEntity {

	@Column(length = 1000)
	private String name;
	@ManyToOne(cascade = CascadeType.REMOVE)
	private BuildRequestToBuildableComponent lastBuildRequestToBuildableComponent;
	@OneToMany(mappedBy = "buildableComponent", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<BuildRequestToBuildableComponent> allBuildRequestToBuildableComponents;
	@Column
	private String buildMode;
	@ManyToOne(optional = false)
	private Environment environment;

	public BuildableComponent(String componentName) {
		name = componentName;
	}

	public BuildableComponent() {
	}

	public static BuildableComponent newComponent(String name) {
		BuildableComponent buildableComponent = new BuildableComponent();
		buildableComponent.setName(name);
		return buildableComponent;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public List<BuildRequestToBuildableComponent> getAllBuildRequestToBuildableComponents() {
		return allBuildRequestToBuildableComponents;
	}

	public void setAllBuildRequestToBuildableComponents(
			List<BuildRequestToBuildableComponent> allBuildRequestToBuildableComponents) {
		this.allBuildRequestToBuildableComponents = allBuildRequestToBuildableComponents;
	}

	public BuildRequestToBuildableComponent getLastBuildRequestToBuildableComponent() {
		return lastBuildRequestToBuildableComponent;
	}

	public void setLastBuildRequestToBuildableComponent(
			BuildRequestToBuildableComponent lastBuildRequestToBuildableComponent) {
		this.lastBuildRequestToBuildableComponent = lastBuildRequestToBuildableComponent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBuildMode() {
		return buildMode;
	}

	public void setBuildMode(String buildMode) {
		this.buildMode = buildMode;
	}

	public BuildRequest createDeploymentRequest() {
		return new BuildRequest(Arrays.asList(this));
	}

	public BuildRequest getLastBuildRequest() {
		if (lastBuildRequestToBuildableComponent != null) {
			return lastBuildRequestToBuildableComponent.getBuildRequest();
		}
		return null;
	}

	public BuildJob getLastBuildJob() {
		if (lastBuildRequestToBuildableComponent != null) {
			return lastBuildRequestToBuildableComponent.getBuildRequest().getBuildJob();
		}
		return null;
	}

	public static class ComponentBuildComparator implements Comparator<BuildableComponent> {
		@Override
		public int compare(BuildableComponent o1, BuildableComponent o2) {
			return ObjectUtils.compare(o1.getName(), o2.getName());
		}
	}
}
