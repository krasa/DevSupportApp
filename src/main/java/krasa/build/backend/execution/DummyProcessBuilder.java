package krasa.build.backend.execution;

import java.util.List;

import krasa.build.backend.execution.process.AbstractProcess;
import krasa.build.backend.execution.process.DummyProcess;
import krasa.build.backend.execution.process.ProcessLog;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("DUMMY")
@Service
public class DummyProcessBuilder extends ProcessBuilder {

	@Override
	protected AbstractProcess getBuildProcess(ProcessLog stringBufferTail, List<String> command) {
		return new DummyProcess(stringBufferTail, command);
	}
}
