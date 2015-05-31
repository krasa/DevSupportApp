package krasa.svn.backend.service.conventions;

import krasa.svn.backend.domain.SvnFolder;

import org.junit.*;

public class TMSvnConventionsStrategyTest {

	@Test
	public void testCoparatorByVersion() throws Exception {

		SvnFolder o1 = new SvnFolder();
		o1.setName("XX_13100");
		SvnFolder o2 = new SvnFolder();
		o2.setName("XX_1000");
		Assert.assertEquals(1, TMSvnConventionsStrategy.TM_NAME_COMPARATOR_BY_VERSION.compare(o1, o2));

		o1.setName("XX_13100");
		o2.setName("XX_100000");
		Assert.assertEquals(-1, TMSvnConventionsStrategy.TM_NAME_COMPARATOR_BY_VERSION.compare(o1, o2));

	}
}
