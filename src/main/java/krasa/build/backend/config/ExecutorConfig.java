package krasa.build.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ExecutorConfig {

	public static final String REFRESH_EXECUTOR = "refreshExecutor";
	public static final String BUILD_EXECUTOR = "buildExecutor";
	public static final String AUTO_MERGE_EXECUTOR = "buildExecutor";
	public static final int MAX_CONCURRENT_BUILDS = 3;
	public static final int MAX_CONCURRENT_MERGES = 3;

	@Bean(name = AUTO_MERGE_EXECUTOR)
	public ThreadPoolTaskExecutor mergeExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(MAX_CONCURRENT_MERGES);
		threadPoolTaskExecutor.setCorePoolSize(MAX_CONCURRENT_MERGES);
		return threadPoolTaskExecutor;
	}

	@Bean(name = BUILD_EXECUTOR)
	public ThreadPoolTaskExecutor buildExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(MAX_CONCURRENT_BUILDS);
		threadPoolTaskExecutor.setCorePoolSize(MAX_CONCURRENT_BUILDS);
		return threadPoolTaskExecutor;
	}

	@Bean(name = REFRESH_EXECUTOR)
	public AsyncTaskExecutor refreshExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(10);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		return threadPoolTaskExecutor;
	}
}
