package krasa.build.backend.execution;

import java.util.Arrays;
import java.util.List;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.execution.process.AbstractProcess;
import krasa.build.backend.execution.process.ProcessLog;
import krasa.build.backend.execution.process.SshjBuildProcess;
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

	public BuildJob create(BuildableComponent buildableComponent) {
		ProcessLog stringBufferTail = new ProcessLog();
		List<String> command = buildableComponent.buildCommand(buildCommandBuilderStrategy);

		AbstractProcess process = getBuildProcess(stringBufferTail, command);
		BuildJob buildJob = new BuildJob(process, buildableComponent);
		buildJob.setCommand(Arrays.toString(command.toArray()));

		process.addListener(buildJob);

		beanFactory.autowireBean(process);

		return buildJob;
	}

	protected AbstractProcess getBuildProcess(ProcessLog stringBufferTail, List<String> command) {
		// return new JschSshBuildProcess(stringBufferTail, command);
		return new SshjBuildProcess(stringBufferTail, command);
	}

}
