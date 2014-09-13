package krasa.core.backend;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentHolder implements EnvironmentAware {

	private static AtomicReference<Environment> environment = new AtomicReference<>();

	public static Environment getEnvironment() {
		return environment.get();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment.set(environment);
	}
}
