package krasa.build.backend.execution;

import java.util.Collections;

import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.backend.execution.process.DummyProcess;
import krasa.merge.backend.dto.BuildRequest;

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
	public ProcessAdapter create(BuildRequest request) {

		ProcessLog log = new ProcessLog();

		DummyProcess process = new DummyProcess(log, Collections.<String> emptyList());
		ProcessAdapter processAdapter = new ProcessAdapter(process, request, log);
		process.addListener(processAdapter);

		beanFactory.autowireBean(processAdapter);
		beanFactory.autowireBean(process);

		return processAdapter;
	}

}
