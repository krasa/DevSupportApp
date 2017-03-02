package krasa.build.backend.facade;

import krasa.build.backend.dto.BuildableComponentDto;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.io.Serializable;

public class ComponentChangedEvent implements Serializable, IWebSocketPushMessage {

	private IPartialPageRequestHandler handler;
	private BuildableComponentDto buildableComponentDto;

	public ComponentChangedEvent(BuildableComponentDto buildableComponentDto) {
		this.buildableComponentDto = buildableComponentDto;
	}

	public BuildableComponentDto getBuildableComponentDto() {
		return buildableComponentDto;
	}

	public IPartialPageRequestHandler getHandler() {
		return handler;
	}

	public void setHandler(IPartialPageRequestHandler handler) {
		this.handler = handler;
	}
}
