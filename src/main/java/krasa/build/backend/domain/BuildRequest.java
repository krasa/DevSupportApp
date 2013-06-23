package krasa.build.backend.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.istack.internal.Nullable;
import krasa.build.backend.execution.strategy.BuildCommandBuilderStrategy;
import krasa.core.backend.domain.AbstractEntity;

import com.google.common.base.Objects;

@Entity
public class BuildRequest extends AbstractEntity {

	@OneToOne(optional = false, cascade = CascadeType.REMOVE)
	private BuildJob buildJob;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "buildRequest", orphanRemoval = true)
	private List<BuildRequestToBuildableComponent> buildRequestToBuildableComponents;
	private String command;

	public BuildRequest() {
	}

	public BuildRequest(BuildableComponent components) {
		this.buildRequestToBuildableComponents = Arrays.asList(new BuildRequestToBuildableComponent(components, this));
	}

	public BuildRequest(List<BuildableComponent> componentsToBuild) {
		this.buildRequestToBuildableComponents = new ArrayList<>();
		for (BuildableComponent buildableComponent : componentsToBuild) {
			buildRequestToBuildableComponents.add(new BuildRequestToBuildableComponent(buildableComponent, this));
		}
	}

	public BuildRequest(BuildableComponent... componentsToBuild) {
		this.buildRequestToBuildableComponents = new ArrayList<>();
		for (BuildableComponent buildableComponent : componentsToBuild) {
			buildRequestToBuildableComponents.add(new BuildRequestToBuildableComponent(buildableComponent, this));
		}
	}

	public BuildJob getBuildJob() {
		return buildJob;
	}

	public void setBuildJob(BuildJob buildJob) {
		this.buildJob = buildJob;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void validate() {
		if ((buildRequestToBuildableComponents == null || buildRequestToBuildableComponents.isEmpty())) {
			throw new IllegalArgumentException("empty component");
		}
	}


	public List<BuildRequestToBuildableComponent> getBuildRequestToBuildableComponents() {
		return buildRequestToBuildableComponents;
	}

	public List<BuildableComponent> getBuildableComponents() {
		return Lists.transform(buildRequestToBuildableComponents, new Function<BuildRequestToBuildableComponent, BuildableComponent>() {
			@Override
			public BuildableComponent apply(@Nullable krasa.build.backend.domain.BuildRequestToBuildableComponent buildRequestToBuildableComponent) {
				return buildRequestToBuildableComponent.getBuildableComponent();
			}
		});
	}

	public void setBuildRequestToBuildableComponents(List<BuildRequestToBuildableComponent> buildRequestToBuildableComponents) {
		this.buildRequestToBuildableComponents = buildRequestToBuildableComponents;
	}

	public boolean contains(BuildableComponent component) {
		return buildRequestToBuildableComponents.contains(component);
	}

	public String getEnvironmentName() {
		String name = null;
		for (BuildableComponent component : getBuildableComponents()) {
			if (name != null && !name.equals(component.getEnvironment().getName())) {
				throw new IllegalArgumentException("multiple environments: " + name + ", " + component);
			}
			name = component.getEnvironment().getName();
		}
		return name;
	}

	public List<String> buildCommand(BuildCommandBuilderStrategy buildCommandBuilderStrategy) {
		List<String> strings = buildCommandBuilderStrategy.toCommand(this);
		this.setCommand(Arrays.toString(strings.toArray()));
		return strings;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("command", command).toString();
	}

	public void setBuildableComponents(List<BuildableComponent> buildableComponents) {
		this.buildRequestToBuildableComponents = new ArrayList<>();
		for (BuildableComponent buildableComponent : buildableComponents) {
			buildRequestToBuildableComponents.add(new BuildRequestToBuildableComponent(buildableComponent, this));
		}

	}
}
