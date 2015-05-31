package krasa.merge.backend.service.automerge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class MergeJobsHolder {
	private static Map<String, AutoMergeProcess> runningTasks = new ConcurrentHashMap<>();
	private static EvictingQueue<AutoMergeProcess> finished = EvictingQueue.create(10);
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public boolean containsKey(String toPath) {
		return runningTasks.containsKey(toPath);
	}

	public AutoMergeProcess get(String id) {
		return runningTasks.get(id);
	}

	public List<AutoMergeProcess> getLastFinished() {
		return Arrays.asList(finished.toArray(new AutoMergeProcess[finished.size()]));
	}

	public void remove(String AutoMergeProcess) {
		AutoMergeProcess remove = runningTasks.remove(AutoMergeProcess);
		finished.add(remove);
	}

	//
	// public void checkPreviousBuilds(BuildableComponent buildableComponent) {
	// AutoMergeProcess lastAutoMergeProcess = buildableComponent.getLastAutoMergeProcess();
	// if (lastAutoMergeProcess != null) {
	// AutoMergeProcess AutoMergeProcess = runningTasks.get(lastAutoMergeProcess.getId());
	// if (AutoMergeProcess != null) {
	// if (AutoMergeProcess.isProcessAlive()) {
	// log.debug("process already running" + AutoMergeProcess);
	// throw new ProcessAlreadyRunning(AutoMergeProcess);
	// }
	// }
	// }
	// }

	public Collection<AutoMergeProcess> getAll() {
		return runningTasks.values();
	}

	public void put(String toPath, AutoMergeProcess autoMergeProcess) {
		runningTasks.put(toPath, autoMergeProcess);
	}
}
