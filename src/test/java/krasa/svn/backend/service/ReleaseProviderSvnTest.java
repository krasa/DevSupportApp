package krasa.svn.backend.service;

import java.util.List;

import junit.framework.Assert;
import krasa.svn.backend.domain.Profile;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Vojtech Krasa
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" })
@Ignore
public class ReleaseProviderSvnTest {

	@Autowired
	SvnReleaseProvider svnReleaseProvider;

	@Test
	public void testGetReleases() throws Exception {
		List<Profile> releases = svnReleaseProvider.getReleases();
		Assert.assertNotNull(releases);
		System.err.println(releases);
	}
}
