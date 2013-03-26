package krasa.build.backend.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.backend.execution.adapter.ProcessAdapterHolder;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.frontend.WicketApplication;
import krasa.merge.backend.domain.Displayable;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.BuildRequest;
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
	private GenericDAO<BuildableComponent> componentBuildDAO;

	@Autowired
	private ProcessAdapterHolder processAdapterHolder;
	@Autowired
	private CommonBuildDao commonBuildDao;
	@Autowired
	private Facade facade;
	@Autowired
	private AsyncTaskExecutor taskExecutor;
	@Autowired
	private krasa.build.backend.execution.ProcessBuilder processBuilder;
	@Autowired
	private TMBuildableComponentResolver buildableComponentResolver;

	@Override
	public synchronized ProcessAdapter build(BuildRequest request) {
		request.validate();
		processAdapterHolder.checkPreviousBuilds(request);

		Environment environment = commonBuildDao.getEnvironmentByName(request.getEnvironmentName());
		List<BuildableComponent> buildableComponents = updateComponents(environment, request.getComponents());
		environmentDAO.save(environment);
		request.setBuildableComponent(buildableComponents);

		ProcessAdapter processAdapter = processBuilder.create(request);
		taskExecutor.submit(processAdapter.getProcess());
		log.info("process started " + request);

		processAdapterHolder.put(processAdapter);

		return processAdapter;
	}

	private List<BuildableComponent> updateComponents(Environment environment, List<String> components) {
		List<BuildableComponent> buildableComponents = new ArrayList<BuildableComponent>();
		Set<String> strings = new HashSet<String>(components);

		for (BuildableComponent buildableComponent : environment.getBuildableComponetns()) {
			if (strings.contains(buildableComponent.getName())) {
				buildableComponent.setStatus(Status.IN_PROGRESS);
				componentBuildDAO.save(buildableComponent);
				buildableComponents.add(buildableComponent);
			}
		}
		return buildableComponents;
	}

	@Override
	public ProcessAdapter refresh(BuildRequest buildRequest) {
		ProcessAdapter processAdapter = processAdapterHolder.get(buildRequest);
		return processAdapter;
	}

	@Override
	public void addBuildableComponent(Environment environment, String componentName) {
		environment = environmentDAO.refresh(environment);
		BuildableComponent component = createComponent(componentName);
		environment.add(component);
		componentBuildDAO.save(component);
		environmentDAO.save(environment);
	}

	@Override
	public void addAllMatchingComponents(Environment environment, String fieldValue) {
		environment = environmentDAO.refresh(environment);
		SvnFolder svnFolder = facade.findBranchByInCaseSensitiveName(fieldValue);
		if (svnFolder != null) {
			addBuildableComponent(environment, svnFolder.getName());
		} else {
			List<SvnFolder> branches = facade.findBranchesByNameLike(fieldValue);
			for (SvnFolder branch : branches) {
				String name = branch.getName();
				BuildableComponent component = createComponent(name);
				environment.add(component);
				componentBuildDAO.save(component);
				environmentDAO.save(environment);
			}
		}
	}

	@Override
	public List<Displayable> getMatchingComponents(String input) {
		List<Displayable> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLikeAsDisplayable(input);
		branchesByNameLikeAsDisplayable.addAll(facade.findTagsByNameLikeAsDisplayable(input));
		return branchesByNameLikeAsDisplayable;
	}

	private BuildableComponent createComponent(String name) {
		return buildableComponentResolver.createComponent(name);
	}

	@Override
	public void onResult(BuildRequest request, ProcessStatus processStatus) {
		List<BuildableComponent> buildableComponent = request.getBuildableComponent();
		for (BuildableComponent build : buildableComponent) {
			BuildableComponent refresh = componentBuildDAO.refresh(build);
			Status status = processStatus.getStatus();
			if (status == Status.SUCCESS) {
				refresh.setLastSuccessBuild(new Date());
			}
			refresh.setStatus(status);
			componentBuildDAO.save(refresh);
		}
		log.debug("sending event REFRESH");
		try {
			EventBus.get(Application.get(WicketApplication.class.getName())).post("REFRESH");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteComponent(Environment environment, BuildableComponent object) {
		environment = environmentDAO.refresh(environment);
		object = componentBuildDAO.refresh(object);

		environment.getBuildableComponetns().remove(object);
		environmentDAO.save(environment);
		componentBuildDAO.delete(object);
	}

	@Override
	public void deleteEnvironment(Integer id) {
		Environment environment = environmentDAO.findById(id);

		List<BuildableComponent> buildableComponents = environment.getBuildableComponetns();
		for (BuildableComponent buildableComponent : buildableComponents) {
			componentBuildDAO.delete(buildableComponent);
		}
		// componentBuilds.clear();
		// environmentDAO.save(environment);
		// environment= environmentDAO.refresh(environment);
		environmentDAO.delete(environment);

	}

	@Override
	public List<Environment> getEnvironments() {
		return environmentDAO.findAll();
	}

	@Override
	public void createEnvironment(String environmentName) {
		List<Environment> by = environmentDAO.findBy(Environment.NAME, environmentName);
		if (by.isEmpty()) {
			environmentDAO.save(new Environment(environmentName));
		} else {
			log.info("environment already created= " + environmentName);
		}
	}

	@Override
	public List<BuildableComponent> getBranchBuilds(Environment environment) {
		Environment byId = environmentDAO.findById(environment.getId());
		List<BuildableComponent> buildableComponents = new ArrayList<BuildableComponent>(byId.getBuildableComponetns());
		Collections.sort(buildableComponents, new BuildableComponent.ComponentBuildComparator());
		return buildableComponents;
	}

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.environmentDAO = genericDAO.build(Environment.class);
		this.environmentDAO = genericDAO.build(Environment.class);
		this.componentBuildDAO = genericDAO.build(BuildableComponent.class);
	}

}
