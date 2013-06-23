package krasa.build.backend.execution;

import java.util.Arrays;
import java.util.Collections;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.execution.process.DummyProcess;
import krasa.build.backend.execution.process.ProcessLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("DUMMY")
@Service
public class DummyProcessBuilder extends ProcessBuilder {
	@Autowired
	AutowireCapableBeanFactory beanFactory;

	@Override
	public BuildJob create(BuildRequest request) {
		ProcessLog log = new ProcessLog();
		request.setCommand(Arrays.toString(request.getBuildableComponents().toArray()));

		DummyProcess process = new DummyProcess(log, Collections.<String> emptyList());
		BuildJob buildJob = new BuildJob(process, request);

		process.addListener(buildJob);

		beanFactory.autowireBean(buildJob);
		beanFactory.autowireBean(process);

		return buildJob;
	}

}
