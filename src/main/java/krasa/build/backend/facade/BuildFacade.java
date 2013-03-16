package krasa.build.backend.facade;

import java.util.List;

import javax.validation.constraints.Null;

import krasa.build.backend.domain.ComponentBuild;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.merge.backend.dto.BuildRequest;

public interface BuildFacade {
	ProcessAdapter build(BuildRequest request);

	List<Environment> getEnvironments();

	void createEnvironment(String environmentName);

	List<ComponentBuild> getBranchBuilds(Environment environment);

	@Null
	ProcessAdapter refresh(BuildRequest buildRequest);

	void addComponnet(Environment object, String branchName);

	void onResult(BuildRequest request, ProcessStatus processStatus);

	void deleteComponent(Environment environment, ComponentBuild object);

	void deleteEnvironment(Integer id);

	void addAllMatchingComponents(Environment object, String fieldValue);
}
