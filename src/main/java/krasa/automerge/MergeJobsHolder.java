package krasa.automerge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class MergeJobsHolder {
	private static Map<String, AutoMergeCommand> runningTasks = new ConcurrentHashMap<>();
	private static EvictingQueue<AutoMergeCommand> finished = EvictingQueue.create(10);
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public boolean containsKey(String toPath) {
		return runningTasks.containsKey(toPath);
	}

	public AutoMergeCommand get(String id) {
		return runningTasks.get(id);
	}

	public List<AutoMergeCommand> getLastFinished() {
		return Arrays.asList(finished.toArray(new AutoMergeCommand[finished.size()]));
	}

	public void remove(String AutoMergeProcess) {
		AutoMergeCommand remove = runningTasks.remove(AutoMergeProcess);
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

	public Collection<AutoMergeCommand> getAll() {
		return runningTasks.values();
	}

	public void put(String toPath, AutoMergeCommand autoMergeCommand) {
		runningTasks.put(toPath, autoMergeCommand);
	}
}
