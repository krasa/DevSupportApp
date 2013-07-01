package krasa.build.backend.facade;

import java.util.List;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.execution.ProcessStatus;
import krasa.merge.backend.domain.Displayable;

public interface BuildFacade {
	BuildJob build(BuildableComponent request);

	List<Environment> getEnvironments();

	Environment createEnvironment(String environmentName) throws AlreadyExistsException;

	List<BuildableComponent> getComponentsByEnvironment(Integer environmentId);

	BuildableComponent createBuildableComponent(Environment environment, String componentName)
			throws AlreadyExistsException;

	void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus);

	void deleteComponentById(final Integer id);

	void deleteEnvironment(Integer id);

	void createBuildableComponentForAllMatchingComponents(Environment object, String fieldValue);

	List<Displayable> getMatchingComponents(String input);

	Environment getEnvironmentByName(String s);

	BuildJob getBuildJobById(Integer id);

	void saveBuildMode(Integer id, String buildMode);

	BuildableComponentDto buildComponent(BuildableComponentDto object);

	BuildJob getBuildJobByComponentId(Integer componentId);

	BuildableComponentDto editBuildableComponent(BuildableComponentDto object);
}
