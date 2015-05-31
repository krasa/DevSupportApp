package krasa.build.backend.facade;

import static org.junit.Assert.*;

import krasa.build.backend.dao.CommonBuildDao;
import krasa.build.backend.domain.*;
import krasa.build.backend.execution.ProcessStatus;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildSvnFacadeTest extends FullTest {

	public static final String ENV = "env";
	@Autowired
	BuildFacade buildFacade;

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
		BuildJob build = buildFacade.createAndSaveBuildJob(buildableComponent, "author");
		assertNotNull(build.getBuildableComponent());
		flush();

		BuildableComponent component = commonBuildDao.refresh(buildableComponent);

		BuildJob lastBuildJob = component.getLastBuildJob();
		assertEquals(build, lastBuildJob);
		assertNotNull(lastBuildJob.getBuildableComponent());
		assertNotNull(lastBuildJob.getBuildableComponent().getLastBuildJob());

		refresh();
		assertEquals(lastBuildJob, buildableComponent.getLastBuildJob());
		assertEquals(1, buildableComponent.getAllBuildJobs().size());

		Environment environment = buildFacade.getEnvironmentByName(ENV);
		assertEquals(2, environment.getBuildableComponents().size());

		flush();
	}

	@Test
	public void testBuildLog() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(buildableComponent, "author");
		build.getProcess().getProcessLog().append("fooBar");
		flush();

		buildFacade.onStatusChanged(build, new ProcessStatus());

		flush();
		BuildJob buildJobById = buildFacade.getBuildJobById(build.getId());
		assertNull(buildJobById.getProcess());
		assertEquals("fooBar", buildJobById.getBuildLog().getLogContent());
		assertEquals("fooBar", buildJobById.getLog().getText());
		assertEquals("", buildJobById.getNextLog(0).getText());
	}

	@Test
	public void testTwoBuilds() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(buildableComponent, "author");
		flush();
		BuildJob lastBuild = buildFacade.createAndSaveBuildJob(buildableComponent, "author");
		flush();

		refresh();
		assertEquals(lastBuild, buildableComponent.getLastBuildJob());
		assertEquals(2, buildableComponent.getAllBuildJobs().size());
		assertEquals(2, commonBuildDao.findAll(buildableComponent).size());

	}

	@Test
	public void testBuildDelete() throws Exception {
		BuildJob build = buildFacade.createAndSaveBuildJob(buildableComponent, "author");
		flush();

		buildFacade.deleteComponentById(buildableComponent.getId());
		flush();

		Environment environment = buildFacade.getEnvironmentByName(ENV);
		assertEquals(1, environment.getBuildableComponents().size());
		assertNull(buildFacade.getBuildJobById(build.getId()));
	}

	private void refresh() {
		buildableComponent = (BuildableComponent) commonBuildDao.refresh(buildableComponent);
	}

}
