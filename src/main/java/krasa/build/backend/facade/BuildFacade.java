package krasa.build.backend.facade;

import java.util.List;

import krasa.build.backend.domain.*;
import krasa.build.backend.dto.*;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.execution.ProcessStatus;
import krasa.merge.backend.domain.Displayable;

public interface BuildFacade {

	List<Environment> getEnvironments();

	Environment createEnvironment(String environmentName) throws AlreadyExistsException;

	List<BuildableComponent> getComponentsByEnvironment(Integer environmentId);

	BuildableComponent createBuildableComponent(Environment environment, String componentName)
			throws AlreadyExistsException;

	void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus);

	void deleteComponentById(final Integer id);

	void deleteEnvironment(Integer id);

	void createBuildableComponentForAllMatchingComponents(Environment object, String fieldValue);

	List<Displayable> getMatchingBranchesAndTags(String input);

	Environment getEnvironmentByName(String s);

	BuildJob getBuildJobById(Integer id);

	void saveBuildMode(Integer id, String buildMode);

	BuildableComponentDto buildComponent(BuildableComponentDto object);

	BuildJob getBuildJobByComponentId(Integer componentId);

	List<BuildJobDto> getRunningBuildJobs();

	BuildableComponentDto editBuildableComponent(BuildableComponentDto object);

	List<BuildJobDto> getLastFinishedBuildJobs();

	void deleteAllBuildableComponents(Environment object);
}
