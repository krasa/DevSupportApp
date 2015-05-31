package krasa.release.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class TokenizationJobsHolder {
	private static Map<Integer, TokenizationJobCommand> runningTasks = new ConcurrentHashMap<>();
	private static EvictingQueue<TokenizationJobCommand> finished = EvictingQueue.create(10);
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public TokenizationJobCommand get(Integer id) {
		return runningTasks.get(id);
	}

	public List<TokenizationJobCommand> getFinished() {
		return Arrays.asList(finished.toArray(new TokenizationJobCommand[finished.size()]));
	}

	public void remove(TokenizationJobCommand tokenizationJobCommand) {
		TokenizationJobCommand remove = runningTasks.remove(tokenizationJobCommand.getJob().getId());
		finished.add(remove);
	}

	public Collection<TokenizationJobCommand> getAll() {
		return runningTasks.values();
	}

	public void put(TokenizationJobCommand tokenizationJobCommand) {
		runningTasks.put(tokenizationJobCommand.getJob().getId(), tokenizationJobCommand);
	}
}
