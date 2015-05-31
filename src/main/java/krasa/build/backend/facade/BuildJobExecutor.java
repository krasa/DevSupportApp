package krasa.build.backend.facade;

import java.util.concurrent.Future;

import krasa.build.backend.domain.BuildJob;

import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.*;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@Service
public class BuildJobExecutor {

	@HystrixCommand(fallbackMethod = "fail", commandProperties = { @HystrixProperty(name = "execution.timeout.enabled", value = "false") }, threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "3"), @HystrixProperty(name = "maxQueueSize", value = "5"),
			@HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
			@HystrixProperty(name = "queueSizeRejectionThreshold", value = "5"),
			@HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
			@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440") })
	public Future<Void> executeBuildJob(final BuildJob buildJob) {
		return new AsyncResult<Void>() {

			@Override
			public Void invoke() {
				buildJob.getBuildJobProcess().execute();
				return null;
			}
		};
	}

	private void fail(BuildJob buildJob) {
		buildJob.kill("hystrix fallback");
	}
}
