package krasa.svn.backend.domain;

import krasa.svn.backend.service.conventions.TMSvnConventionsStrategy;

import org.junit.Before;

/**
 * @author Vojtech Krasa
 */
public class ConventionsDependentStrategyTest {

	protected TMSvnConventionsStrategy TMSvnConventionsStrategy;

	@Before
	public void setUp() throws Exception {
		TMSvnConventionsStrategy = new TMSvnConventionsStrategy();
	}
}
