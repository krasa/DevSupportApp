package krasa.build.backend.execution;

import java.util.List;

import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.backend.execution.process.SshBuildProcess;
import krasa.build.backend.execution.strategy.BuildCommandBuilderStrategy;
import krasa.merge.backend.dto.BuildRequest;

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

	public ProcessAdapter create(BuildRequest request) {
		StringBufferTail stringBufferTail = new StringBufferTail();
		List<String> command = buildCommandBuilderStrategy.toCommand(request);

		SshBuildProcess process = new SshBuildProcess(stringBufferTail, command);
		ProcessAdapter processAdapter = new ProcessAdapter(process, request, stringBufferTail);
		process.addListener(processAdapter);

		beanFactory.autowireBean(processAdapter);
		beanFactory.autowireBean(process);

		return processAdapter;
	}

}
