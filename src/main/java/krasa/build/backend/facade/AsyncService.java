package krasa.build.backend.facade;

import krasa.build.backend.config.ExecutorConfig;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.core.frontend.WicketApplication;

import org.apache.wicket.Application;
import org.apache.wicket.atmosphere.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Async(ExecutorConfig.REFRESH_EXECUTOR)
	public void sendRefresh(BuildJob buildJob) {
		BuildableComponent buildableComponent = buildJob.getBuildableComponent();
		log.debug("sending event REFRESH, component={}, status={}", buildableComponent.getName(), buildJob.getStatus());
		try {
			ComponentChangedEvent event = new ComponentChangedEvent(new BuildableComponentDto(
					buildableComponent));
			EventBus.get(Application.get(WicketApplication.class.getName())).post(event);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
