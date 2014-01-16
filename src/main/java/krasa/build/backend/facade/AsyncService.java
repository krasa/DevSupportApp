package krasa.build.backend.facade;

import krasa.build.backend.config.ExecutorConfig;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.dto.BuildableComponentDto;

import org.apache.wicket.Application;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AsyncService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Async(ExecutorConfig.REFRESH_EXECUTOR)
	public void sendRefresh(BuildJob buildJob) {
		BuildableComponent buildableComponent = buildJob.getBuildableComponent();
		log.debug("sending event REFRESH, component={}, status={}", buildableComponent.getName(), buildJob.getStatus());
		try {
			ComponentChangedEvent event = new ComponentChangedEvent(new BuildableComponentDto(buildableComponent));
			EventBus.get(getApplication()).post(event);
			EventBus.get(getApplication()).post(new ComponentBuildEvent());
		} catch (IllegalArgumentException e) {
			// TODO wicket bug?
			if (e.getMessage().equals("Argument 'page' may not be null.")) {
				log.error("sendRefresh:" + e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private Application getApplication() {
		Application application = Application.get(WicketFilter.class.getName());
		Assert.notNull(application);
		return application;
	}
}
