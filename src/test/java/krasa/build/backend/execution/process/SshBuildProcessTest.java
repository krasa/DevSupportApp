package krasa.build.backend.execution.process;

import org.junit.Assert;
import org.junit.Test;

public class SshBuildProcessTest {
	@Test
	public void testGetExitStatusFromLog() throws Exception {
		int exitStatusFromLog = SshBuildProcess.getExitStatusFromLog(0,
				"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
						+ "[E|2013/03/12 14:02:26] Build of component [xxxxx] returned code [ 1 ]\n"
						+ "[xxxxx@xxxxxxxx ~]$ exit");
		Assert.assertEquals(1, exitStatusFromLog);
	}
}
