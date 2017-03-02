package krasa.core.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import krasa.build.backend.config.ExecutorConfig;

@Service
public class ShutdownManager {

	@Autowired
	private ApplicationContext appContext;

	@Async(ExecutorConfig.SHUTDOWN_EXECUTOR)
	public void initiateShutdown(int returnCode) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SpringApplication.exit(appContext, () -> returnCode);
	}
}
