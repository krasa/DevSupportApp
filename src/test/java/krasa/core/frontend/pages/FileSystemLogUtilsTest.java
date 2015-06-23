package krasa.core.frontend.pages;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;

import krasa.build.backend.dto.LogFileDto;

import org.junit.*;

public class FileSystemLogUtilsTest {
	protected List<File> files = new ArrayList<>();

	@After
	public void tearDown() throws Exception {
		for (File file : files) {
			file.delete();
		}
	}

	@Test
	public void testReadLogFileWithSizeLimit() throws Exception {
		File file = newFile();
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(0, logFileDto.getOffset());
		assertEquals("", logFileDto.getText());
	}

	private File newFile() throws IOException {
		File file = new File("target/", String.valueOf(System.currentTimeMillis()));
		while (file.exists()) {
			file = new File("target/", String.valueOf(System.currentTimeMillis()));
		}
		file.createNewFile();
		files.add(file);
		return file;
	}
}