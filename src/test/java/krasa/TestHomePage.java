package krasa;

import krasa.core.frontend.WicketApplication;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoPage;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
@Ignore
public class TestHomePage {
	private WicketTester tester;

	@Before
	public void setUp() {
		tester = new WicketTester(new WicketApplication());
	}

	@Test
	public void homepageRendersSuccessfully() {
		// start and render the test page
		tester.startPage(MergeInfoPage.class);

		// assert rendered page class
		tester.assertRenderedPage(MergeInfoPage.class);
	}
}
