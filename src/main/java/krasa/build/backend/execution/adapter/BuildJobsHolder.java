package krasa.build.backend.execution.adapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import krasa.build.backend.domain.*;
import krasa.build.backend.exception.ProcessAlreadyRunning;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class BuildJobsHolder {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static Map<Integer, BuildJob> buildJobHashMap = new ConcurrentHashMap<>();
	private static EvictingQueue<BuildJob> finished = EvictingQueue.create(10);

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
}
