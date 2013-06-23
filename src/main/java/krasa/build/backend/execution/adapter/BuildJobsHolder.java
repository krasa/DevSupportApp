package krasa.build.backend.execution.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.Null;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.exception.ProcessAlreadyRunning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BuildJobsHolder {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static Map<Integer, BuildJob> buildJobHashMap = new ConcurrentHashMap<>();

	@Null
	public BuildJob get(BuildJob request) {
		return buildJobHashMap.get(request.getId());
	}

	public void put(BuildJob job) {
		buildJobHashMap.put(job.getId(), job);
	}

	public BuildJob get(Integer id) {
		return buildJobHashMap.get(id);
	}

	public void remove(BuildJob buildJob) {
		buildJobHashMap.remove(buildJob.getId());
	}

	public void checkPreviousBuilds(BuildRequest request) {
		for (BuildableComponent buildableComponent : request.getBuildableComponents()) {
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != null) {
				BuildJob buildJob = buildJobHashMap.get(lastBuildJob.getId());
				if (buildJob != null) {
					if (buildJob.isAlive()) {
						log.debug("process already running" + buildJob);
						throw new ProcessAlreadyRunning(buildJob);
					}
				}
			}
		}
	}
}
