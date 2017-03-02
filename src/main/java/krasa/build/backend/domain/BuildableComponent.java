package krasa.build.backend.domain;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.ObjectUtils;

import krasa.build.backend.execution.strategy.BuildCommandBuilderStrategy;
import krasa.core.backend.domain.AbstractEntity;

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
	@Column
	public Date lastSuccessBuildDuration;
	@Column
	private Integer buildOrder = 0;
	@Column
	private Boolean build;

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

	public Date getLastSuccessBuildDuration() {
		return lastSuccessBuildDuration;
	}

	public void setLastSuccessBuildDuration(Date lastSuccessBuildDuration) {
		this.lastSuccessBuildDuration = lastSuccessBuildDuration;
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

	public Integer getBuildOrder() {
		if (buildOrder == null) {
			if (name.contains("MYSQL_PORTAL_"))
				setBuildOrder(0);
			else if (name.contains("MYSQL_"))
				setBuildOrder(20);
			else if (name.contains("msc-data"))
				setBuildOrder(50);
			else
				setBuildOrder(100);
		}
		return buildOrder;
	}

	public void setBuildOrder(Integer buildOrder) {
		this.buildOrder = buildOrder;
	}

	public Boolean isBuild() {
		if (build == null) {
			return false;
		}
		return build;
	}

	public void setBuild(Boolean build) {
		this.build = build;
	}

	public static class ComponentBuildComparator implements Comparator<BuildableComponent> {

		@Override
		public int compare(BuildableComponent o1, BuildableComponent o2) {
			return ObjectUtils.compare(o1.getName(), o2.getName());
		}
	}
}
