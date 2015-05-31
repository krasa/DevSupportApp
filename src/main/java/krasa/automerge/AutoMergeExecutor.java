package krasa.automerge;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.*;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@Service
public class AutoMergeExecutor {

	@HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.timeout.enabled", value = "false")

	}, threadPoolProperties = { @HystrixProperty(name = "coreSize", value = "3"),
			@HystrixProperty(name = "maxQueueSize", value = "15"),
			@HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
			@HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
			@HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
			@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440") })
	public Future<Void> executeAutoMerge(final AutoMergeProcess autoMergeProcess) {
		return new AsyncResult<Void>() {

			@Override
			public Void invoke() {
				autoMergeProcess.run();
				return null;
			}
		};
	}
}
