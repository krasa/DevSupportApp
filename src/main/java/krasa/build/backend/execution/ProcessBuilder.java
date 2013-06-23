package krasa.build.backend.execution;

import java.util.List;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.execution.process.ProcessLog;
import krasa.build.backend.execution.process.SshBuildProcess;
import krasa.build.backend.execution.strategy.BuildCommandBuilderStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!DUMMY")
@Service
public class ProcessBuilder {
	@Autowired
	AutowireCapableBeanFactory beanFactory;
	@Autowired
	BuildCommandBuilderStrategy buildCommandBuilderStrategy;

	public BuildJob create(BuildRequest request) {
		ProcessLog stringBufferTail = new ProcessLog();
		List<String> command = request.buildCommand(buildCommandBuilderStrategy);

		SshBuildProcess process = new SshBuildProcess(stringBufferTail, command);
		BuildJob buildJob = new BuildJob(process, request);

		process.addListener(buildJob);

		beanFactory.autowireBean(process);

		return buildJob;
	}

}
