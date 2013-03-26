package krasa.build.backend.execution;

import krasa.build.backend.dto.Result;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class ProcessLogTest {

	public static final int COUNT = 3000;
	int i = 0;
	protected ProcessLog processLog;

	@Test
	public void testGetNext() throws Exception {
		processLog = new ProcessLog();
		String random = RandomStringUtils.randomAlphabetic(COUNT);
		processLog.append(random);

		Result next = processLog.getNext(0);
		Assert.assertEquals(COUNT, next.getLength());
		Assert.assertEquals(random, next.getText());

		processLog.append("," + ++i);
		processLog.append("," + ++i);
		processLog.append("," + ++i);
		processLog.append("," + ++i);
		processLog.append("," + ++i);

		next = processLog.getNext(0);
		Assert.assertEquals(COUNT + 5, next.getLength());
		Assert.assertEquals(random + ",1,2,3,4,5", next.getText());

		for (int j = 0; j < 5000; j++) {
			processLog.append("," + ++i);
		}
		next = processLog.getNext(0);
		Assert.assertEquals(COUNT + 5005, next.getLength());
		Assert.assertTrue(next.getText().substring(COUNT).startsWith("...2505 lines skipped"));
		Assert.assertTrue(next.getText().endsWith("5005"));

	}
}
