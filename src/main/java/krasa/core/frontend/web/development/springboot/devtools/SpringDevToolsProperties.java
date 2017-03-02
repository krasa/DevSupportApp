package krasa.core.frontend.web.development.springboot.devtools;

import org.springframework.boot.context.properties.ConfigurationProperties;

import krasa.core.frontend.web.development.devutils.inspector.InspectorProperties;

@ConfigurationProperties(prefix = InspectorProperties.PROPERTY_PREFIX)
public class SpringDevToolsProperties {

	public static final String PROPERTY_PREFIX = "spring.devtools.restart";

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
