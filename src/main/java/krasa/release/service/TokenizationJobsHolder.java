package krasa.release.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class TokenizationJobsHolder {
	private static Map<Integer, TokenizationProcess> runningTasks = new ConcurrentHashMap<>();
	private static EvictingQueue<TokenizationProcess> finished = EvictingQueue.create(10);
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public TokenizationProcess get(Integer id) {
		return runningTasks.get(id);
	}

	public List<TokenizationProcess> getFinished() {
		return Arrays.asList(finished.toArray(new TokenizationProcess[finished.size()]));
	}

	public void remove(TokenizationProcess tokenizationProcess) {
		TokenizationProcess remove = runningTasks.remove(tokenizationProcess.getJob().getId());
		finished.add(remove);
	}

	public Collection<TokenizationProcess> getAll() {
		return runningTasks.values();
	}

	public void put(TokenizationProcess tokenizationProcess) {
		runningTasks.put(tokenizationProcess.getJob().getId(), tokenizationProcess);
	}
}
