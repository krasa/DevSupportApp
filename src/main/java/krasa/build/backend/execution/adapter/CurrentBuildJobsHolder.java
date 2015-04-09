package krasa.build.backend.execution.adapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.*;

import krasa.build.backend.domain.*;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.facade.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class CurrentBuildJobsHolder {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static Map<Integer, BuildJob> buildJobHashMap = new ConcurrentHashMap<>();
	private static EvictingQueue<BuildJob> finished = EvictingQueue.create(10);
	@Autowired
	private EventService eventService;

	@PostConstruct
	private void init() {
		SimpleAsyncTaskExecutor serviceExecutor = new SimpleAsyncTaskExecutor("BuildJobsHolderDaemon-");
		serviceExecutor.setDaemon(true);
		serviceExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					new BuildJobsDaemon().start();
				} catch (InterruptedException e) {
					log.error("BuildJobsDaemon interrupted", e);
				}
			}
		});
	}

	@Nullable
	public BuildJob get(BuildJob request) {
		return buildJobHashMap.get(request.getId());
	}

	public void put(BuildJob job) {
		buildJobHashMap.put(job.getId(), job);
	}

	public BuildJob get(Integer id) {
		return buildJobHashMap.get(id);
	}

	public List<BuildJob> getLastFinished() {
		return Arrays.asList(finished.toArray(new BuildJob[finished.size()]));
	}

	public void remove(BuildJob buildJob) {
		finished.add(buildJob);
		buildJobHashMap.remove(buildJob.getId());
	}

	public void checkPreviousBuilds(BuildableComponent buildableComponent) {
		BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
		if (lastBuildJob != null) {
			BuildJob buildJob = buildJobHashMap.get(lastBuildJob.getId());
			if (buildJob != null) {
				if (buildJob.isProcessAlive()) {
					log.debug("process already running" + buildJob);
					throw new ProcessAlreadyRunning(buildJob);
				}
			}
		}
	}

	public Collection<BuildJob> getAll() {
		return buildJobHashMap.values();
	}

	public boolean isEmpty() {
		return buildJobHashMap.isEmpty();
	}

	class BuildJobsDaemon {

		boolean wasEmpty = true;
		boolean broken = false;

		public void start() throws InterruptedException {
			while (true) {
				try {
					boolean empty = isEmpty();
					if (!empty || !wasEmpty) {
						log.debug("sending CurrentlyBuildingUpdate");
						eventService.sendEvent(CurrentlyBuildingUpdate.INSTANCE);
						wasEmpty = empty;
					}
				} catch (Exception e) {
					if (!broken) {
						log.error("", e);
					}
					broken = true;
				}
				Thread.sleep(1000);
			}
		}
	}
}
