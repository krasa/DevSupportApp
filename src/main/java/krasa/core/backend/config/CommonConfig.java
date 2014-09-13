package krasa.core.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class CommonConfig {

	@Autowired
	protected Environment environment;

	public String get(CommonConfigProperties key) {
		return environment.getRequiredProperty(key.getValue());
	}

	public String get(String key) {
		return environment.getRequiredProperty(key);
	}

	public String getOptional(String key) {
		return environment.getProperty(key);
	}

	public String getOptional(CommonConfigProperties key) {
		return environment.getProperty(key.getValue());
	}

	public Integer getInteger(CommonConfigProperties key) {
		return environment.getRequiredProperty(key.getValue(), Integer.class);
	}

	public Integer getInteger(String key) {
		return environment.getRequiredProperty(key, Integer.class);
	}

	public Integer getIntegerOptional(CommonConfigProperties key) {
		return environment.getProperty(key.getValue(), Integer.class);
	}

	public Integer getIntegerOptional(String key) {
		return environment.getProperty(key, Integer.class);
	}

	public Long getLong(CommonConfigProperties key) {
		return environment.getRequiredProperty(key.getValue(), Long.class);
	}

	public Long getLong(String key) {
		return environment.getRequiredProperty(key, Long.class);
	}

	public Long getLongOptional(CommonConfigProperties key) {
		return environment.getProperty(key.getValue(), Long.class);
	}

	public Long getLongOptional(String key) {
		return environment.getProperty(key, Long.class);
	}

	public boolean getBoolean(CommonConfigProperties key) {
		return environment.getRequiredProperty(key.getValue(), Boolean.class);
	}

	public boolean getBoolean(String key) {
		return environment.getRequiredProperty(key, Boolean.class);
	}

	public boolean getBooleanOptional(CommonConfigProperties key) {
		return environment.getProperty(key.getValue(), Boolean.class);
	}

	public boolean getBooleanOptional(String key) {
		return environment.getProperty(key, Boolean.class);
	}

	public Environment getEnvironment() {
		return environment;
	}
}
