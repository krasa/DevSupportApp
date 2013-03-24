package krasa.build.backend.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.ComponentBuild;
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
	private GenericDAO<ComponentBuild> componentBuildDAO;

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

	@Override
	public synchronized ProcessAdapter build(BuildRequest request) {
		request.validate();
		processAdapterHolder.checkPreviousBuilds(request);

		Environment environment = commonBuildDao.getEnvironmentByName(request.getEnvironmentName());
		List<ComponentBuild> componentBuilds = updateComponents(environment, request.getComponents());
		environmentDAO.save(environment);
		request.setComponentBuild(componentBuilds);

		ProcessAdapter processAdapter = processBuilder.create(request);
		taskExecutor.submit(processAdapter.getProcess());
		log.info("process started " + request);

		processAdapterHolder.put(processAdapter);

		return processAdapter;
	}

	private List<ComponentBuild> updateComponents(Environment environment, List<String> components) {
		List<ComponentBuild> componentBuilds = new ArrayList<ComponentBuild>();
		Set<String> strings = new HashSet<String>(components);

		for (ComponentBuild componentBuild : environment.getComponentBuilds()) {
			if (strings.contains(componentBuild.getName())) {
				componentBuild.setStatus(Status.IN_PROGRESS);
				componentBuildDAO.save(componentBuild);
				componentBuilds.add(componentBuild);
			}
		}
		return componentBuilds;
	}

	@Override
	public ProcessAdapter refresh(BuildRequest buildRequest) {
		ProcessAdapter processAdapter = processAdapterHolder.get(buildRequest);
		return processAdapter;
	}

	@Override
	public void addComponnet(Environment object, String branchName) {
		object = environmentDAO.refresh(object);
		addComponent(object, branchName);
		environmentDAO.save(object);
	}

	@Override
	public void addAllMatchingComponents(Environment object, String fieldValue) {
		object = environmentDAO.refresh(object);
		SvnFolder svnFolder = facade.findBranchByInCaseSensitiveName(fieldValue);
		if (svnFolder != null) {
			addComponnet(object, svnFolder.getName());
		} else {
			List<SvnFolder> branches = facade.findBranchesByNameLike(fieldValue);
			for (SvnFolder branch : branches) {
				String name = branch.getName();
				addComponent(object, name);
			}
		}
	}

	@Override
	public List<Displayable> getMatchingComponents(String input) {
		List<Displayable> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLikeAsDisplayable(input);
		branchesByNameLikeAsDisplayable.addAll(facade.findTagsByNameLikeAsDisplayable(input));
		return branchesByNameLikeAsDisplayable;
	}

	private void addComponent(Environment object, String name) {
		ComponentBuild componentBuild = ComponentBuild.newComponent(name);
		componentBuild.setEnvironment(object);
		componentBuildDAO.save(componentBuild);
		object.getComponentBuilds().add(componentBuild);
	}

	@Override
	public void onResult(BuildRequest request, ProcessStatus processStatus) {
		List<ComponentBuild> componentBuild = request.getComponentBuild();
		for (ComponentBuild build : componentBuild) {
			ComponentBuild refresh = componentBuildDAO.refresh(build);
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
	public void deleteComponent(Environment environment, ComponentBuild object) {
		environment = environmentDAO.refresh(environment);
		object = componentBuildDAO.refresh(object);

		environment.getComponentBuilds().remove(object);
		environmentDAO.save(environment);
		componentBuildDAO.delete(object);
	}

	@Override
	public void deleteEnvironment(Integer id) {
		Environment environment = environmentDAO.findById(id);

		List<ComponentBuild> componentBuilds = environment.getComponentBuilds();
		for (ComponentBuild componentBuild : componentBuilds) {
			componentBuildDAO.delete(componentBuild);
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
	public List<ComponentBuild> getBranchBuilds(Environment environment) {
		Environment byId = environmentDAO.findById(environment.getId());
		List<ComponentBuild> componentBuilds = new ArrayList<ComponentBuild>(byId.getComponentBuilds());
		Collections.sort(componentBuilds, new ComponentBuild.ComponentBuildComparator());
		return componentBuilds;
	}

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.environmentDAO = genericDAO.build(Environment.class);
		this.environmentDAO = genericDAO.build(Environment.class);
		this.componentBuildDAO = genericDAO.build(ComponentBuild.class);
	}

}
