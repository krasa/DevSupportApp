package krasa.merge.backend.domain;

import java.util.List;

import junit.framework.Assert;
import krasa.merge.backend.service.conventions.TMSvnConventionsStrategy;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class ConventionsDependentStrategyTest {

	protected TMSvnConventionsStrategy TMSvnConventionsStrategy;

	@Before
	public void setUp() throws Exception {
		TMSvnConventionsStrategy = new TMSvnConventionsStrategy();
	}

	@Test
	public void test_getAlphabeticallyLowerBranchNameForMatchAll() throws Exception {
		SvnFolder folder = new SvnFolder();
		folder.setName("PORTAL_7202");
		List<String> alphabeticlyLowerBranchName = TMSvnConventionsStrategy.getAlphabeticallyLowerBranchNameForMatchAll(folder);
		Assert.assertEquals("PORTAL_72", alphabeticlyLowerBranchName.get(0));

	}

	@Test
	public void test_getAlphabeticallyLowerBranchNameForMatchAll3() throws Exception {
		SvnFolder folder = new SvnFolder();
		folder.setName("PORTAL_7200");
		List<String> alphabeticlyLowerBranchName = TMSvnConventionsStrategy.getAlphabeticallyLowerBranchNameForMatchAll(folder);
		Assert.assertEquals("PORTAL_71", alphabeticlyLowerBranchName.get(0));

	}

	@Test
	public void test_getAlphabeticallyLowerBranchNameForMatchAll2() throws Exception {
		SvnFolder folder = new SvnFolder();
		folder.setName("MYSql_PORTAL_7202");
		List<String> alphabeticlyLowerBranchName = TMSvnConventionsStrategy.getAlphabeticallyLowerBranchNameForMatchAll(folder);
		Assert.assertEquals("MYSql_PORTAL_72", alphabeticlyLowerBranchName.get(0));

	}
}
