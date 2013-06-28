package krasa.build.backend.domain;

import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import krasa.build.backend.execution.strategy.BuildCommandBuilderStrategy;
import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.ObjectUtils;

@Entity
public class BuildableComponent extends AbstractEntity {

	@Column(length = 1000)
	private String name;
	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private BuildJob lastBuildJob;
	@OneToMany(mappedBy = "buildableComponent", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<BuildJob> allBuildJobs;
	@Column
	private String buildMode;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
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

	public List<BuildJob> getAllBuildJobs() {
		return allBuildJobs;
	}

	public void setAllBuildJobs(List<BuildJob> allBuildJobs) {
		this.allBuildJobs = allBuildJobs;
	}

	public BuildJob getLastBuildJob() {
		return lastBuildJob;
	}

	public void setLastBuildJob(BuildJob lastBuildJob) {
		this.lastBuildJob = lastBuildJob;
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

	public List<String> buildCommand(BuildCommandBuilderStrategy buildCommandBuilderStrategy) {
		return buildCommandBuilderStrategy.toCommand(this);
	}

	public static class ComponentBuildComparator implements Comparator<BuildableComponent> {
		@Override
		public int compare(BuildableComponent o1, BuildableComponent o2) {
			return ObjectUtils.compare(o1.getName(), o2.getName());
		}
	}
}
