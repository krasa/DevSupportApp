package krasa.build.backend.execution;

import java.util.List;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.execution.process.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("DUMMY")
@Service
public class DummyProcessBuilder extends ProcessBuilder {

	@Override
	protected AbstractProcess getBuildProcess(ProcessLog stringBufferTail, List<String> command, BuildJob buildJob) {
		return new DummyProcess(stringBufferTail, command, buildJob);
	}
}
