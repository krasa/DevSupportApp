package krasa.build.backend.execution;

import java.util.*;

import krasa.build.backend.domain.*;
import krasa.build.backend.execution.process.*;
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

	public BuildJob create(BuildableComponent buildableComponent, String author) {
		List<String> command = buildableComponent.buildCommand(buildCommandBuilderStrategy);

		BuildJob buildJob = new BuildJob(buildableComponent);
		buildJob.setCommand(Arrays.toString(command.toArray()));
		buildJob.setCaller(author);

		BuildJobProcess process = getBuildProcess(command, buildJob);
		buildJob.setBuildJobProcess(process);

		process.addListener(buildJob);

		beanFactory.autowireBean(process);

		return buildJob;
	}

	protected BuildJobProcess getBuildProcess(List<String> command, BuildJob buildJob) {
		// return new JschSshBuildProcess(stringBufferTail, command);
		return new SshjBuildProcess(command, buildJob);
	}

}
