package krasa.build.backend.execution.process;

import org.junit.Assert;
import org.junit.Test;

public class JschSshBuildProcessTest {
	@Test
	public void testGetExitStatusFromLog() throws Exception {
		int exitStatusFromLog = new JschSshBuildProcess().getExitStatusFromLog(0,
				"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
						+ "[E|2013/03/12 14:02:26] Build of component [xxxxx] returned code [ 1 ]\n"
						+ "[xxxxx@xxxxxxxx ~]$ exit");
		Assert.assertEquals(1, exitStatusFromLog);
	}

	@Test
	public void testGetExitStatusFromLog2() throws Exception {
		int exitStatusFromLog = new JschSshBuildProcess().getExitStatusFromLog(0,
				"[I|2013/04/08 14:42:53] Build of component [cccc-xxx] returned code [ 0 ]\n"
						+ "[xxxxx@ssssss ~]$ exit\n" + "logout");
		Assert.assertEquals(1, exitStatusFromLog);
	}
}
