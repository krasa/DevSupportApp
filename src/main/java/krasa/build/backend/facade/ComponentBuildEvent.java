package krasa.build.backend.facade;

import krasa.build.backend.domain.BuildJob;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class ComponentBuildEvent implements IWebSocketPushMessage {
	private final String buildableComponentName;
	private final Integer environmentId;

	public ComponentBuildEvent(BuildJob buildJob) {
		environmentId = buildJob.getBuildableComponent().getEnvironment().getId();
		buildableComponentName = buildJob.getBuildableComponent().getName();
	}

	public String getBuildableComponentName() {
		return buildableComponentName;
	}

	public Integer getEnvironmentId() {
		return environmentId;
	}
}
