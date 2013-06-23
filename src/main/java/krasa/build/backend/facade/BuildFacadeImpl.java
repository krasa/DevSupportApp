package krasa.build.backend.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.domain.BuildRequestToBuildableComponent;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.domain.Status;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.adapter.BuildJobsHolder;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.frontend.WicketApplication;
import krasa.merge.backend.domain.Displayable;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.Application;
import org.apache.wicket.atmosphere.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BuildFacadeImpl implements BuildFacade {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected GenericDAO<Environment> environmentDAO;
	private GenericDAO<BuildableComponent> buildableComponentDAO;
	private GenericDAO<BuildJob> buildJobDAO;

	@Autowired
	private BuildJobsHolder runningBuildJobsHolder;
	@Autowired
	private CommonBuildDao commonBuildDao;
	@Autowired
	private Facade facade;
	@Autowired
	private AsyncTaskExecutor taskExecutor;
	@Autowired
	private krasa.build.backend.execution.ProcessBuilder processBuilder;

	@Override
	public synchronized BuildJob build(BuildRequest request) {
		request.validate();
		checkPreviousBuilds(request);

		BuildJob buildJob = createAndSaveBuildJob(request);
		commonBuildDao.flush();

		runningBuildJobsHolder.put(buildJob);
		taskExecutor.submit(buildJob.getProcess());
		log.info("process started " + request.toString());

		return buildJob;
	}

	protected BuildJob createAndSaveBuildJob(BuildRequest request) {
		refreshComponents(request);
		BuildJob buildJob = processBuilder.create(request);
		buildJobDAO.save(buildJob);
		return buildJob;
	}

	private void refreshComponents(BuildRequest request) {
		List<BuildableComponent> buildableComponents = new ArrayList<BuildableComponent>();
		for (BuildableComponent buildableComponent : request.getBuildableComponents()) {
			buildableComponents.add(refresh(buildableComponent));
		}
		request.setBuildableComponents(buildableComponents);
	}

	private void checkPreviousBuilds(BuildRequest request) {
		runningBuildJobsHolder.checkPreviousBuilds(request);
	}

	private void updateComponents(BuildJob buildJob) {
		List<BuildableComponent> buildRequestToBuildableComponents = buildJob.getRequest().getBuildableComponents();
		for (BuildableComponent buildableComponent : buildRequestToBuildableComponents) {
			buildableComponentDAO.save(buildableComponent);
		}
	}

	@Override
	public BuildableComponent createBuildableComponent(Environment environment, String componentName) {
		environment = environmentDAO.refresh(environment);
		BuildableComponent buildableComponent = environment.addBuildableComponent(componentName);
		buildableComponentDAO.save(buildableComponent);
		return buildableComponent;
	}

	@Override
	public void createBuildableComponentForAllMatchingComponents(Environment environment, String fieldValue) {
		environment = environmentDAO.refresh(environment);
		List<SvnFolder> branches = facade.findBranchesByNameLike(fieldValue);
		for (SvnFolder branch : branches) {
			String name = branch.getName();
			createBuildableComponent(environment, name);
		}
		environmentDAO.save(environment);
	}

	@Override
	public List<Displayable> getMatchingComponents(String input) {
		List<Displayable> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLikeAsDisplayable(input);
		branchesByNameLikeAsDisplayable.addAll(facade.findTagsByNameLikeAsDisplayable(input));
		return branchesByNameLikeAsDisplayable;
	}

	@Override
	public Environment getEnvironmentByName(String s) {
		return environmentDAO.findOneBy("name", s);
	}

	private BuildableComponent refresh(BuildableComponent object) {
		return buildableComponentDAO.refresh(object);
	}

	@Override
	public BuildJob getBuildJobById(Integer id) {
		BuildJob buildJob = runningBuildJobsHolder.get(id);
		if (buildJob == null) {
			buildJob = buildJobDAO.findById(id);
		}
		return buildJob;
	}

	@Override
	public void saveBuildMode(Integer id, String buildMode) {
		commonBuildDao.updateBuildMode(id, buildMode);
	}

	@Override
	public void buildComponent(BuildableComponentDto object) {
		BuildableComponent byId = buildableComponentDAO.findById(object.getId());
		build(byId.createDeploymentRequest());
	}

	@Override
	public BuildJob getBuildJobByComponentId(Integer componentId) {
		BuildableComponent byId = buildableComponentDAO.findById(componentId);
		return byId.getLastBuildJob();
	}

	@Override
	public void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus) {
		buildJob.setStatus(processStatus.getStatus());
		if (!processStatus.isAlive()) {
			buildJob.setEndTime(new Date());
			runningBuildJobsHolder.remove(buildJob);
		}
		buildJob.onBeforeSave();
		buildJobDAO.merge(buildJob);
		log.debug("sending event REFRESH");
		try {
			EventBus.get(Application.get(WicketApplication.class.getName())).post("REFRESH");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteComponentById(final Integer id) {
		final BuildableComponent byId = buildableComponentDAO.findById(id);
		final List<BuildRequestToBuildableComponent> allBuildRequestToBuildableComponents = byId.getAllBuildRequestToBuildableComponents();
		for (BuildRequestToBuildableComponent allBuildRequestToBuildableComponent : allBuildRequestToBuildableComponents) {
			if (allBuildRequestToBuildableComponent.getBuildRequest().getBuildableComponents().size() == 1) {
				commonBuildDao.delete(allBuildRequestToBuildableComponent.getBuildJob());
			}
		}
		buildableComponentDAO.delete(byId);
	}

	@Override
	public void deleteEnvironment(Integer id) {
		Environment environment = environmentDAO.findById(id);

		List<BuildableComponent> buildableComponents = environment.getBuildableComponents();
		for (BuildableComponent buildableComponent : buildableComponents) {
			buildableComponentDAO.delete(buildableComponent);
		}
		// componentBuilds.clear();
		// environmentDAO.save(environment);
		// environment= environmentDAO.refresh(environment);
		environmentDAO.delete(environment);

	}

	@Override
	public List<Environment> getEnvironments() {
		return Environment.sortByName(environmentDAO.findAll());
	}

	@Override
	public Environment createEnvironment(String environmentName) throws AlreadyExistsException {
		List<Environment> by = environmentDAO.findBy(Environment.NAME, environmentName);
		if (by.isEmpty()) {
			return environmentDAO.save(new Environment(environmentName));
		} else {
			throw new AlreadyExistsException(environmentName);
		}
	}

	@Override
	public List<BuildableComponent> getComponentsByEnvironment(Environment environment) {
		environment = environmentDAO.findById(environment.getId());
		List<BuildableComponent> buildableComponents = new ArrayList<BuildableComponent>(
				environment.getBuildableComponents());
		checkInProgressStatus(buildableComponents);
		Collections.sort(buildableComponents, new BuildableComponent.ComponentBuildComparator());
		return buildableComponents;
	}

	private void checkInProgressStatus(List<BuildableComponent> buildableComponents) {
		for (BuildableComponent buildableComponent : buildableComponents) {
			BuildRequest latestBuildRequest = buildableComponent.getLastBuildRequest();
			if (latestBuildRequest != null) {
				BuildJob lastBuildJob = latestBuildRequest.getBuildJob();
				if (lastBuildJob != null && lastBuildJob.getStatus() == Status.IN_PROGRESS
						&& runningBuildJobsHolder.get(lastBuildJob) == null) {
					lastBuildJob.setStatus(Status.KILLED);
					buildJobDAO.save(lastBuildJob);
				}
			}
		}
	}

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.environmentDAO = genericDAO.build(Environment.class);
		this.environmentDAO = genericDAO.build(Environment.class);
		this.buildableComponentDAO = genericDAO.build(BuildableComponent.class);
		this.buildJobDAO = genericDAO.build(BuildJob.class);
	}

}
