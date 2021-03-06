package krasa.build.backend.facade;

import java.util.Collection;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import krasa.build.backend.config.ExecutorConfig;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.core.frontend.web.WicketWebInitializer;

@Service
public class EventService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Async(ExecutorConfig.REFRESH_EXECUTOR)
	public void sendRefresh(BuildJob buildJob) {
		BuildableComponent buildableComponent = buildJob.getBuildableComponent();
		log.debug("sending event REFRESH, component={}, status={}", buildableComponent.getName(), buildJob.getStatus());
		try {
			sendEvent(new ComponentChangedEvent(new BuildableComponentDto(buildableComponent, buildJob)));
		} catch (IllegalArgumentException e) {
			// TODO wicket bug?
			if (e.getMessage().equals("Argument 'page' may not be null.")) {
				log.error("sendRefresh:" + e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	public void sendEvent(IWebSocketPushMessage event) {
		Application application = Application.get(WicketWebInitializer.WICKET_FILTERNAME);
		if (application != null) {
			WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
			IWebSocketConnectionRegistry connectionRegistry = webSocketSettings.getConnectionRegistry();
			Collection<IWebSocketConnection> connections = connectionRegistry.getConnections(application);
			log.trace("sending event to {} connections", connections.size());
			for (IWebSocketConnection connection : connections) {
				connection.sendMessage(event);
			}
		}
	}

	private Application getApplication() {
		Application application = Application.get(WicketFilter.class.getName());
		Assert.notNull(application);
		return application;
	}

}
