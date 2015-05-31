package krasa.core.backend.common;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.*;
import org.springframework.core.io.*;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ExternalPropertiesLoader implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private AbstractEnvironment environment;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Properties props = new Properties();
		load(props, environment.getProperty("externalProperties"));
		environment.getPropertySources().addFirst(new PropertiesPropertySource("custom", props));
	}

	private void load(Properties props, String path) {
		if (path != null) {
			Resource classPathResource = new FileSystemResource(path);
			if (classPathResource.exists()) {
				try {
					PropertiesLoaderUtils.fillProperties(props, classPathResource);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				log.info(path + " loaded");
			} else {
				log.info(path + " does not exist");
			}
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (AbstractEnvironment) environment;

	}

	@Override
	public int getOrder() {
		return 0;
	}
}
