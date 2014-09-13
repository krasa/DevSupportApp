package krasa.build.backend.facade;

import java.util.*;

import krasa.build.backend.config.ExecutorConfig;
import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.*;
import krasa.build.backend.dto.*;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.adapter.BuildJobsHolder;
import krasa.core.backend.RemoteHostUtils;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.facade.Facade;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
	@Qualifier(ExecutorConfig.BUILD_EXECUTOR)
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private krasa.build.backend.execution.ProcessBuilder processBuilder;
	@Autowired
	EventService eventService;

	protected BuildJob createAndSaveBuildJob(BuildableComponent buildableComponent, String author) {
		buildableComponent = refresh(buildableComponent);
		BuildJob buildJob = processBuilder.create(buildableComponent, author);
		buildableComponent.setLastBuildJob(buildJob);
		buildJobDAO.save(buildJob);
		return buildJob;
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public BuildableComponent createBuildableComponent(Environment environment, String componentName)
			throws AlreadyExistsException {
		if (StringUtils.isBlank(componentName)) {
			return null;
		}
		environment = environmentDAO.refresh(environment);
		BuildableComponent buildableComponent = environment.addBuildableComponent(componentName);
		buildableComponentDAO.save(buildableComponent);
		return buildableComponent;
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void createBuildableComponentForAllMatchingComponents(Environment environment, String fieldValue) {
		environment = environmentDAO.refresh(environment);
		List<SvnFolder> branches = facade.findBranchesByNameLike(fieldValue);
		for (SvnFolder branch : branches) {
			String name = branch.getName();
			try {
				createBuildableComponent(environment, name);
			} catch (AlreadyExistsException e) {
				// ok
			}
		}
		environmentDAO.save(environment);
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Displayable> getMatchingComponents(String input) {
		List<Displayable> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLikeAsDisplayable(input);
		branchesByNameLikeAsDisplayable.addAll(facade.findTagsByNameLikeAsDisplayable(input));
		return branchesByNameLikeAsDisplayable;
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Environment getEnvironmentByName(String s) {
		return environmentDAO.findOneBy("name", s);
	}

	private BuildableComponent refresh(BuildableComponent object) {
		return buildableComponentDAO.refresh(object);
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public BuildJob getBuildJobById(Integer id) {
		BuildJob buildJob = runningBuildJobsHolder.get(id);
		if (buildJob == null) {
			buildJob = buildJobDAO.findById(id);
		}
		return buildJob;
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void saveBuildMode(Integer id, String buildMode) {
		commonBuildDao.updateBuildMode(id, buildMode);
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public BuildableComponentDto buildComponent(BuildableComponentDto object) {
		BuildableComponent buildableComponent = buildableComponentDAO.findById(object.getId());

		BuildJob build = build(buildableComponent, RemoteHostUtils.getRemoteHost());
		return BuildableComponentDto.transform(build.getBuildableComponent());
	}

	private BuildJob build(BuildableComponent buildableComponent, String author) {
		runningBuildJobsHolder.checkPreviousBuilds(buildableComponent);
		BuildJob buildJob = createAndSaveBuildJob(buildableComponent, author);
		commonBuildDao.flush();

		runningBuildJobsHolder.put(buildJob);
		taskExecutor.submit(buildJob.getProcess());
		log.info("process scheduled " + buildableComponent.toString());
		return buildJob;
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public BuildJob getBuildJobByComponentId(Integer componentId) {
		BuildableComponent byId = buildableComponentDAO.findById(componentId);
		return byId.getLastBuildJob();
	}

	@Override
	public List<BuildJobDto> getRunningBuildJobs() {
		Collection<BuildJob> all = runningBuildJobsHolder.getAll();
		return BuildJobDto.translate(all);
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public BuildableComponentDto editBuildableComponent(BuildableComponentDto object) {
		BuildableComponent byId = buildableComponentDAO.findById(object.getId());
		byId.setName(object.getName());
		byId.setBuildMode(object.getBuildMode());
		buildableComponentDAO.save(byId);
		return BuildableComponentDto.transform(byId);
	}

	@Override
	public List<BuildJobDto> getLastFinishedBuildJobs() {
		List<BuildJobDto> translate = BuildJobDto.translate(runningBuildJobsHolder.getLastFinished());
		Collections.reverse(translate);
		return translate;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	@Override
	public void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus) {
		buildJob.setStatus(processStatus.getStatus());
		if (processStatus.isAlive()) {
			buildJob.setStartTime(new Date());
		} else {
			buildJob.setEndTime(new Date());
			if (processStatus.getStatus() == Status.SUCCESS) {
				BuildableComponent buildableComponent = buildJob.getBuildableComponent();
				Date lastSuccessBuildDuration = new Date(buildJob.getEndTime().getTime()
						- buildJob.getStartTime().getTime());
				buildableComponent.setLastSuccessBuildDuration(lastSuccessBuildDuration);
				buildableComponentDAO.save(buildableComponent);
			}
			runningBuildJobsHolder.remove(buildJob);
		}
		buildJob.onBeforeSave();
		buildJobDAO.save(buildJob);
		eventService.sendRefresh(buildJob);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	@Override
	public void deleteComponentById(final Integer id) {
		final BuildableComponent byId = buildableComponentDAO.findById(id);
		byId.getEnvironment().getBuildableComponents().remove(byId);
		buildableComponentDAO.delete(byId);
		buildableComponentDAO.flush();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	@Override
	public void deleteEnvironment(Integer id) {
		Environment environment = environmentDAO.findById(id);
		environmentDAO.delete(environment);
	}

	@Override
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Environment> getEnvironments() {
		return Environment.sortByName(environmentDAO.findAll());
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
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
	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<BuildableComponent> getComponentsByEnvironment(Integer environmentId) {
		Environment environment = environmentDAO.findById(environmentId);
		List<BuildableComponent> buildableComponents1 = environment.getBuildableComponents();
		List<BuildableComponent> buildableComponents = new ArrayList<>(buildableComponents1);
		checkInProgressStatus(buildableComponents);
		Collections.sort(buildableComponents, new BuildableComponent.ComponentBuildComparator());
		return buildableComponents;
	}

	private void checkInProgressStatus(List<BuildableComponent> buildableComponents) {
		for (BuildableComponent buildableComponent : buildableComponents) {
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != null) {
				BuildJob buildJob = runningBuildJobsHolder.get(lastBuildJob);
				if (lastBuildJob.isNotFinished() && buildJob == null) {
					lastBuildJob.setStatus(Status.KILLED);
					buildJobDAO.save(lastBuildJob);
				} else if (lastBuildJob.isNotFinished() && buildJob != null
						&& !buildJob.getProcess().getStatus().isAlive()) {
					lastBuildJob.setStatus(buildJob.getProcess().getStatus().getStatus());
					BuildLog buildLog = lastBuildJob.getBuildLog();
					if (buildLog == null) {
						// todo wtf
						buildLog = new BuildLog();
						lastBuildJob.setBuildLog(buildLog);
					}
					buildLog.setLogContent(buildLog.getLogContent() + "\n-------------------\nProcess died\n"
							+ ExceptionUtils.getStackTrace(buildJob.getProcess().getStatus().getException()));
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
