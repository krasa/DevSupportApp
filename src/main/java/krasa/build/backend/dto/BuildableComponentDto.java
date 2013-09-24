package krasa.build.backend.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Status;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class BuildableComponentDto implements Serializable {
	private Integer id;
	private String name;

	private Integer environmentId;

	private String buildMode;
	private Integer buildJobId;
	private Date buildEndTime;
	private Date buildStartTime;
	private Status status;

	public BuildableComponentDto() {
	}

	public BuildableComponentDto(BuildableComponent component) {
		id = component.getId();
		name = component.getName();
		environmentId = component.getEnvironment().getId();
		buildMode = component.getBuildMode();
		BuildJob lastBuildJob = component.getLastBuildJob();
		if (lastBuildJob != null) {
			buildJobId = lastBuildJob.getId();
			buildEndTime = lastBuildJob.getEndTime();
			buildStartTime = lastBuildJob.getStartTime();
			status = lastBuildJob.getStatus();
		}
	}

	public static List<BuildableComponentDto> transform(List<BuildableComponent> components) {
		return Lists.transform(components, new Function<BuildableComponent, BuildableComponentDto>() {
			@Override
			public BuildableComponentDto apply(BuildableComponent component) {
				return new BuildableComponentDto(component);
			}
		});
	}

	public static BuildableComponentDto transform(BuildableComponent component) {
		return new BuildableComponentDto(component);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(Integer environmentId) {
		this.environmentId = environmentId;
	}

	public Date getBuildStartTime() {
		return buildStartTime;
	}

	public void setBuildStartTime(Date buildStartTime) {
		this.buildStartTime = buildStartTime;
	}

	public String getBuildMode() {
		return buildMode;
	}

	public void setBuildMode(String buildMode) {
		this.buildMode = buildMode;
	}

	public Integer getBuildJobId() {
		return buildJobId;
	}

	public void setBuildJobId(Integer buildJobId) {
		this.buildJobId = buildJobId;
	}

	public Date getBuildEndTime() {
		return buildEndTime;
	}

	public void setBuildEndTime(Date buildEndTime) {
		this.buildEndTime = buildEndTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getTotalTime() {
		if (buildEndTime == null) {
			return null;
		}
		return new Date(buildEndTime.getTime() - buildStartTime.getTime());
	}
}
