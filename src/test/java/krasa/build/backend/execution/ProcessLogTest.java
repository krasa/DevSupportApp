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

		for (int j = 0; j < 2000; j++) {
			processLog.append("," + ++i);
		}

		next = processLog.getNext(0);
		Assert.assertEquals(COUNT + 2005, next.getLength());
		Assert.assertTrue(next.getText().endsWith("2005"));

		for (int j = 0; j < 3000; j++) {
			processLog.append("," + ++i);
		}

		next = processLog.getNext(0);
		Assert.assertEquals(COUNT + 5005, next.getLength());
		Assert.assertEquals(next.getText().substring(COUNT + 1).substring(0, "...2505 lines skipped".length()),
				"...2505 lines skipped");
		Assert.assertTrue(next.getText().endsWith("5005"));

		next = processLog.getNext(COUNT);
		Assert.assertEquals(COUNT + 5005, next.getLength());
		Assert.assertEquals(next.getText().substring(1).substring(0, "...2505 lines skipped".length()),
				"...2505 lines skipped");
		Assert.assertTrue(next.getText().endsWith("5005"));

		next = processLog.getNext(COUNT + 5000);
		Assert.assertEquals(COUNT + 5005, next.getLength());
		Assert.assertEquals(next.getText(), ",5001,5002,5003,5004,5005");

	}
}
