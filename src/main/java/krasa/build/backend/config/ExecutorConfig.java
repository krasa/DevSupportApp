package krasa.build.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ExecutorConfig {

	public static final String REFRESH_EXECUTOR = "refreshTaskExecutor";
	public static final String SHUTDOWN_EXECUTOR = "SHUTDOWN_EXECUTOR";
	public static final int MAX_CONCURRENT_TOKENIZATIONS = 3;
	public static final String TOKENIZATION_EXECUTOR = "tokenizationTaskExecutor";

	@Bean(name = TOKENIZATION_EXECUTOR)
	public ThreadPoolTaskExecutor tokenizationExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(MAX_CONCURRENT_TOKENIZATIONS);
		threadPoolTaskExecutor.setCorePoolSize(MAX_CONCURRENT_TOKENIZATIONS);
		return threadPoolTaskExecutor;
	}

	@Bean(name = REFRESH_EXECUTOR)
	public AsyncTaskExecutor refreshExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(10);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		return threadPoolTaskExecutor;
	}

	@Bean(name = SHUTDOWN_EXECUTOR)
	public AsyncTaskExecutor shutdownExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
}
