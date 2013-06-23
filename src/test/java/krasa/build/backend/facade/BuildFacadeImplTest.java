package krasa.build.backend.facade;

import static org.junit.Assert.*;
import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.domain.FullTest;
import krasa.build.backend.execution.ProcessStatus;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildFacadeImplTest extends FullTest {

	public static final String ENV = "env";
	@Autowired
	BuildFacadeImpl buildFacade;
	private Environment environment;
	private BuildableComponent buildableComponent2;
	private BuildableComponent buildableComponent;

	@Autowired
	private CommonBuildDao commonBuildDao;

	@Before
	public void setUp() throws Exception {
		environment = buildFacade.createEnvironment(ENV);
		buildableComponent = buildFacade.createBuildableComponent(environment, "foo");
		buildableComponent2 = buildFacade.createBuildableComponent(environment, "bar");

	}

	@Test
	public void testCreateAndSaveBuildJob() throws Exception {
		assertNotNull(buildableComponent.getId());
		BuildJob build = buildFacade.createAndSaveBuildJob(new BuildRequest(buildableComponent, buildableComponent2));
		assertNotNull(build.getRequest());
		flush();

		BuildableComponent component = commonBuildDao.refresh(buildableComponent);

		BuildRequest latestBuildRequest = component.getLastBuildRequest();
		assertNotNull(latestBuildRequest);
		BuildJob lastBuildJob = latestBuildRequest.getBuildJob();
		assertEquals(build, lastBuildJob);
		assertNotNull(lastBuildJob.getRequest());
		assertNotNull(lastBuildJob.getRequest().getBuildJob());

		assertEquals(2, lastBuildJob.getRequest().getBuildRequestToBuildableComponents().size());
		assertEquals(ENV, lastBuildJob.getRequest().getEnvironmentName());

		refresh();
		assertEquals(lastBuildJob, buildableComponent.getLastBuildJob());
		assertEquals(1, buildableComponent.getAllBuildRequestToBuildableComponents().size());

		Environment environment = buildFacade.getEnvironmentByName(ENV);
		assertEquals(2, environment.getBuildableComponents().size());

		flush();
		commonBuildDao.delete(commonBuildDao.refresh(buildableComponent));
		flush();
	}

	@Test
	public void testBuildLog() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(new BuildRequest(buildableComponent, buildableComponent2));
		build.getProcess().getProcessLog().append("fooBar");
		flush();

		buildFacade.onStatusChanged(build, new ProcessStatus());

		flush();
		final BuildJob buildJobById = buildFacade.getBuildJobById(build.getId());
		assertNull(buildJobById.getProcess());
		assertEquals("fooBar", buildJobById.getBuildLog().getLogContent());
		assertEquals("fooBar", buildJobById.getLog().getText());
		assertEquals("", buildJobById.getNextLog(0).getText());

		buildFacade.deleteComponentById(buildableComponent.getId());
		flush();
	}

	@Test
	public void testTwoBuilds() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(new BuildRequest(buildableComponent, buildableComponent2));
		flush();
		BuildJob lastBuild = buildFacade.createAndSaveBuildJob(new BuildRequest(buildableComponent, buildableComponent2));
		flush();

		refresh();
		assertEquals(lastBuild, buildableComponent.getLastBuildJob());
		assertEquals(2, buildableComponent.getAllBuildRequestToBuildableComponents().size());
		assertEquals(2, commonBuildDao.findAll(buildableComponent).size());

	}

	@Test
	public void testBuildDelete() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(new BuildRequest(buildableComponent, buildableComponent2));
		flush();

		buildFacade.deleteComponentById(buildableComponent.getId());
		flush();

		Environment environment = buildFacade.getEnvironmentByName(ENV);
		assertEquals(1, environment.getBuildableComponents().size());
		assertNotNull(buildFacade.getBuildJobById(build.getId()));

		buildFacade.deleteComponentById(buildableComponent2.getId());
		flush();

		assertEquals(0, environment.getBuildableComponents().size());
		assertNull(buildFacade.getBuildJobById(build.getId()));
	}

	private void refresh() {
		buildableComponent = (BuildableComponent) commonBuildDao.refresh(buildableComponent);
	}

}
