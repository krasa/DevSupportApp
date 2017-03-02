package krasa.build.backend.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import krasa.build.backend.DateUtils;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Status;
import krasa.core.frontend.commons.table.CustomIdTableItem;

public class BuildableComponentDto implements Serializable, CustomIdTableItem {

	private Integer componentId;
	private String name;

	private Integer environmentId;
	private Boolean build;
	private Integer buildOrder;

	private String buildMode;
	private Integer buildJobId;
	private Date buildEndTime;
	private Date buildStartTime;
	private Date buildScheduledTime;
	private Status status;
	private int index = -1;

	public BuildableComponentDto() {
	}

	public BuildableComponentDto(BuildableComponent component) {
		this(component, component.getLastBuildJob());
	}

	public BuildableComponentDto(BuildableComponent component, BuildJob buildJob) {
		componentId = component.getId();
		name = component.getName();
		environmentId = component.getEnvironment().getId();
		buildMode = component.getBuildMode();
		build = component.isBuild();
		buildOrder = component.getBuildOrder();
		if (buildJob != null) {
			buildJobId = buildJob.getId();
			buildEndTime = buildJob.getEndTime();
			buildStartTime = buildJob.getStartTime();
			buildScheduledTime = buildJob.getScheduledTime();
			status = buildJob.getStatus();
		}
	}

	public BuildableComponentDto(Integer componentId) {
		this.componentId = componentId;
	}

	public static List<BuildableComponentDto> transform(List<BuildableComponent> components) {
		List<BuildableComponentDto> transform = Lists.transform(components,
				new Function<BuildableComponent, BuildableComponentDto>() {

					@Override
					public BuildableComponentDto apply(BuildableComponent component) {
						return new BuildableComponentDto(component);
					}
				});
		transform = Lists.newArrayList(transform);

		List<BuildableComponentDto> buildableComponentDtos = new ArrayList<>();
		buildableComponentDtos.addAll(transform);
		Collections.sort(buildableComponentDtos, new Comparator<BuildableComponentDto>() {

			@Override
			public int compare(BuildableComponentDto o1, BuildableComponentDto o2) {
				return DateUtils.compareDatesNullOnEnd(o1.buildScheduledTime, o2.buildScheduledTime);
			}
		});
		for (int i = 0; i < buildableComponentDtos.size(); i++) {
			if (buildableComponentDtos.get(i).buildScheduledTime != null) {
				buildableComponentDtos.get(i).setIndex(i);
			}
		}

		return transform;
	}

	private void setIndex(int i) {
		index = i;
	}

	public int getIndex() {
		return index;
	}

	public static BuildableComponentDto transform(BuildableComponent component) {
		return new BuildableComponentDto(component);
	}

	public boolean isBuild() {
		return build != null && build;
	}

	public void setBuild(boolean build) {
		this.build = build;
	}

	public Integer getBuildOrder() {
		return buildOrder;
	}

	public void setBuildOrder(Integer buildOrder) {
		this.buildOrder = buildOrder;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
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
		if (buildEndTime == null || buildStartTime == null) {
			return null;
		}
		return new Date(buildEndTime.getTime() - buildStartTime.getTime());
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

	@Override
	public String getRowId() {
		return String.valueOf(componentId);
	}

	public static BuildableComponentDto byId(Integer componentId) {
		return new BuildableComponentDto(componentId);
	}
}
