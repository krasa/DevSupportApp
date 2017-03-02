package krasa.core.frontend.web.actuator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class WicketEndpointRepositoryDefault implements WicketEndpointRepository {

	public List<WicketAutoConfig> wicketAutoConfigurations = new ArrayList<>();
	
	public void add(WicketAutoConfig autoconfig) {
		this.wicketAutoConfigurations.add(autoconfig);
	}

	@Override
	public List<WicketAutoConfig> getConfigs() {
		return wicketAutoConfigurations;
	}
	
}
