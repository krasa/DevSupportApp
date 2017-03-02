package krasa.build.backend.facade;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.execution.process.BuildJobProcess;

@Service
public class BuildJobExecutor {

	private static final Logger log = LoggerFactory.getLogger(BuildJobExecutor.class);
	private ConcurrentHashMap<String, ReentrantLock> s = new ConcurrentHashMap<>();
	private Semaphore semaphore = new Semaphore(3);

	@HystrixCommand(fallbackMethod = "fail", commandProperties = {
			@HystrixProperty(name = "execution.timeout.enabled", value = "false") }, threadPoolProperties = {
					@HystrixProperty(name = "coreSize", value = "30"),
					@HystrixProperty(name = "maxQueueSize", value = "30"),
					@HystrixProperty(name = "keepAliveTimeMinutes", value = "30"),
					@HystrixProperty(name = "queueSizeRejectionThreshold", value = "30"),
					@HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
					@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
			})
	public synchronized Future<Void> executeBuildJob(final BuildJob buildJob) {
		return new AsyncResult<Void>() {

			@Override
			public Void invoke() {
				String environment = buildJob.getBuildableComponent().getEnvironment().getName();
				ReentrantLock reentrantLock = s.get(environment);
				if (reentrantLock == null) {
					reentrantLock = new ReentrantLock();
					s.put(environment, reentrantLock);
				}

				BuildJobProcess buildJobProcess = buildJob.getBuildJobProcess();
				buildJobProcess.execute(reentrantLock, semaphore);
				return null;
			}
		};
	}

	private void fail(BuildJob buildJob) {
		log.info("build failed - hystrix fallback " + buildJob);
		buildJob.kill("hystrix fallback");
	}
}
