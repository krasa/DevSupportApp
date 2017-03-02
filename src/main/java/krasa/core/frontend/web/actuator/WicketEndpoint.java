package krasa.core.frontend.web.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;

@ConfigurationProperties(prefix = "endpoints.wicket")
public class WicketEndpoint extends AbstractEndpoint<Map<String, Object>> implements EnvironmentAware {

	private WicketEndpointRepository repository;

	public WicketEndpoint(WicketEndpointRepository repository) {
		super("wicket");
		this.repository = repository;
	}

	@Override
	public Map<String, Object> invoke() {
		Map<String, Object> result = new HashMap<>();
		result.put("extensions", repository.getConfigs());
		return result;
	}

}
