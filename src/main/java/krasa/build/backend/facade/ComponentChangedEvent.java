package krasa.build.backend.facade;

import java.io.Serializable;

import krasa.build.backend.dto.BuildableComponentDto;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class ComponentChangedEvent implements Serializable {

	private AjaxRequestTarget target;
	private BuildableComponentDto buildableComponentDto;

	public ComponentChangedEvent(BuildableComponentDto buildableComponentDto) {
		this.buildableComponentDto = buildableComponentDto;
	}

	public BuildableComponentDto getBuildableComponentDto() {
		return buildableComponentDto;
	}

	public AjaxRequestTarget getTarget() {
		return target;
	}

	public void setTarget(AjaxRequestTarget target) {
		this.target = target;
	}
}
