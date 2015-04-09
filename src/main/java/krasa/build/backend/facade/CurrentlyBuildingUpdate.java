package krasa.build.backend.facade;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class CurrentlyBuildingUpdate implements IWebSocketPushMessage {

	public static final CurrentlyBuildingUpdate INSTANCE = new CurrentlyBuildingUpdate();
}
