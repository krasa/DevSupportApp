package krasa.release.tokenization;

import static krasa.release.tokenization.Default.toPomVersion;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultTest {

	@Test
	public void testToPomVersion() throws Exception {
		assertEquals("99.9.9-SNAPSHOT", toPomVersion("9999"));
		assertEquals("14.1.0.0", toPomVersion("14100"));
		assertEquals("14.1.1.0", toPomVersion("14110"));
		assertEquals("14.1.0.1", toPomVersion("14101"));
		assertEquals("14.1.1.1", toPomVersion("14111"));
	}
}
