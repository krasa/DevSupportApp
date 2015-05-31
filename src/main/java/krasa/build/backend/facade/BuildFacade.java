package krasa.build.backend.facade;

import java.util.*;

import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.*;
import krasa.build.backend.dto.*;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.adapter.CurrentBuildJobsHolder;
import krasa.core.backend.RemoteHostUtils;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.svn.backend.domain.*;
import krasa.svn.backend.facade.SvnFacade;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuildFacade {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected GenericDAO<Environment> environmentDAO;
	private GenericDAO<BuildableComponent> buildableComponentDAO;
	private GenericDAO<BuildJob> buildJobDAO;

	@Autowired
	private CurrentBuildJobsHolder runningCurrentBuildJobsHolder;
	@Autowired
	private CommonBuildDao commonBuildDao;
	@Autowired
	private SvnFacade facade;
	@Autowired
	private krasa.build.backend.execution.ProcessBuilder processBuilder;
	@Autowired
	EventService eventService;
	@Autowired
	BuildJobExecutor buildJobExecutor;
	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sf;

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void cleanHsqldb() {
		log.info("cleaning Hsqldb");
		deleteOldBuildJobs();
		limitNumberOfBuildJobs();
	}

	@SuppressWarnings({ "JpaQlInspection", "unchecked" })
	private void limitNumberOfBuildJobs() {
		List<Long> ids = sf.getCurrentSession().createQuery("select id from BuildJob order by id desc").setMaxResults(
				200).list();
		int deleted = 0;
		List<BuildJob> list = sf.getCurrentSession().createQuery("from BuildJob where id not in :ids").setParameterList(
				"ids", ids).setMaxResults(1000).list();
		for (BuildJob o : list) {
			BuildableComponent buildableComponent = o.getBuildableComponent();
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != o) {
				log.info("deleting {}", o);
				sf.getCurrentSession().delete(o);
				deleted++;
			} else {
				buildableComponent.setLastBuildJob(null);
				sf.getCurrentSession().save(buildableComponent);
				log.info("deleting {}", o);
				sf.getCurrentSession().delete(o);
				deleted++;
			}
		}
		log.info("deleted: {}", deleted);
	}

	private void deleteOldBuildJobs() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -7);

		Session session = sf.getCurrentSession();
		Criteria crit = session.createCriteria(BuildJob.class);
		crit.add(Restrictions.le("endTime", cal.getTime()));
		List<BuildJob> list = crit.list();
		log.info("found: {}", list.size());
		int deleted = 0;
		for (BuildJob o : list) {
			BuildableComponent buildableComponent = o.getBuildableComponent();
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != o) {
				log.info("deleting {}", o);
				session.delete(o);
				deleted++;
			}
		}
		log.info("deleted: {}", deleted);
	}

	protected BuildJob createAndSaveBuildJob(BuildableComponent buildableComponent, String author) {
		buildableComponent = refresh(buildableComponent);
		BuildJob buildJob = processBuilder.create(buildableComponent, author);
		buildableComponent.setLastBuildJob(buildJob);
		buildJobDAO.save(buildJob);
		return buildJob;
	}

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

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Displayable> getMatchingBranchesAndTags(String input) {
		List<Displayable> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLikeAsDisplayable(input);
		branchesByNameLikeAsDisplayable.addAll(facade.findTagsByNameLikeAsDisplayable(input));
		return branchesByNameLikeAsDisplayable;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Environment getEnvironmentByName(String s) {
		return environmentDAO.findOneBy("name", s);
	}

	private BuildableComponent refresh(BuildableComponent object) {
		return buildableComponentDAO.refresh(object);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public BuildJob getBuildJobById(Integer id) {
		BuildJob buildJob = runningCurrentBuildJobsHolder.get(id);
		if (buildJob == null) {
			buildJob = buildJobDAO.findById(id);
		}
		return buildJob;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void saveBuildMode(Integer id, String buildMode) {
		commonBuildDao.updateBuildMode(id, buildMode);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public BuildableComponentDto buildComponent(BuildableComponentDto object) {
		BuildableComponent buildableComponent = buildableComponentDAO.findById(object.getComponentId());

		BuildJob build = build(buildableComponent, getCaller());
		return BuildableComponentDto.transform(build.getBuildableComponent());
	}

	private BuildJob build(BuildableComponent buildableComponent, String author) {
		runningCurrentBuildJobsHolder.checkPreviousBuilds(buildableComponent);
		BuildJob buildJob = createAndSaveBuildJob(buildableComponent, author);
		commonBuildDao.flush();

		runningCurrentBuildJobsHolder.put(buildJob);
		buildJobExecutor.executeBuildJob(buildJob);
		log.info("process scheduled " + buildableComponent.toString());
		return buildJob;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public BuildJob getBuildJobByComponentId(Integer componentId) {
		BuildableComponent byId = buildableComponentDAO.findById(componentId);
		return byId.getLastBuildJob();
	}

	public List<BuildJobDto> getRunningBuildJobs() {
		Collection<BuildJob> all = runningCurrentBuildJobsHolder.getAll();
		return BuildJobDto.translate(all);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public BuildableComponentDto editBuildableComponent(BuildableComponentDto object) {
		BuildableComponent byId = buildableComponentDAO.findById(object.getComponentId());
		byId.setName(object.getName());
		byId.setBuildMode(object.getBuildMode());
		buildableComponentDAO.save(byId);
		return BuildableComponentDto.transform(byId);
	}

	public List<BuildJobDto> getLastFinishedBuildJobs() {
		List<BuildJobDto> translate = BuildJobDto.translate(runningCurrentBuildJobsHolder.getLastFinished());
		Collections.reverse(translate);
		return translate;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void deleteAllBuildableComponents(Environment object) {
		Environment environment = environmentDAO.findById(object.getId());
		environment.getBuildableComponents().clear();
		environmentDAO.save(environment);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
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
			runningCurrentBuildJobsHolder.remove(buildJob);
		}
		buildJobDAO.save(buildJob);
		eventService.sendRefresh(buildJob);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void deleteComponentById(Integer id) {
		BuildableComponent byId = buildableComponentDAO.findById(id);
		byId.getEnvironment().getBuildableComponents().remove(byId);
		buildableComponentDAO.delete(byId);
		buildableComponentDAO.flush();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void deleteEnvironment(Integer id) {
		Environment environment = environmentDAO.findById(id);
		environmentDAO.delete(environment);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Environment> getEnvironments() {
		return Environment.sortByName(environmentDAO.findAll());
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public Environment createEnvironment(String environmentName) throws AlreadyExistsException {
		List<Environment> by = environmentDAO.findBy(Environment.NAME, environmentName);
		if (by.isEmpty()) {
			return environmentDAO.save(new Environment(environmentName));
		} else {
			throw new AlreadyExistsException(environmentName);
		}
	}

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
				BuildJob runningBuildJob = runningCurrentBuildJobsHolder.get(lastBuildJob);
				if (lastBuildJob.isNotFinished() && runningBuildJob == null) {
					lastBuildJob.setStatus(Status.KILLED);
					buildJobDAO.save(lastBuildJob);
				} else if (lastBuildJob.isNotFinished() && runningBuildJob != null
						&& !runningBuildJob.getBuildJobProcess().getStatus().isAlive()) {
					// fallback
					lastBuildJob.setStatus(runningBuildJob.getBuildJobProcess().getStatus().getStatus());
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

	public static String getCaller() {
		String remoteHost = RemoteHostUtils.getRemoteHost();
		remoteHost = remoteHost.replace("0:0:0:0:0:0:0:1", "local");
		return remoteHost;
	}

}
