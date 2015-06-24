package krasa.core.frontend.pages;

import static org.junit.Assert.assertEquals;

import java.io.*;

import krasa.build.backend.dto.LogFileDto;

import org.apache.commons.io.FileUtils;
import org.junit.*;

public class FileSystemLogUtilsTest {

	private File file;

	@Before
	public void setUp() throws Exception {
		file = newFile();
	}

	@After
	public void tearDown() throws Exception {
		file.delete();
	}

	@Test
	public void emptyFile() throws Exception {
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(0, logFileDto.getOffset());
		assertEquals("", logFileDto.getText());
	}

	@Test
	public void oneFullBuffer() throws Exception {
		FileUtils.write(file, "12345");
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(5, logFileDto.getOffset());
		assertEquals("12345", logFileDto.getText());
	}

	@Test
	public void twoBuffers() throws Exception {
		FileUtils.write(file, "1234567890");
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(10, logFileDto.getOffset());
		assertEquals("1234567890", logFileDto.getText());
	}

	@Test
	public void threeBuffers() throws Exception {
		FileUtils.write(file, "123456789012345");
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(15, logFileDto.getOffset());
		assertEquals("12345\n<<<<<<<<<<<<<<<<<<<<< FILE TOO LONG, SKIPPING 5 chars >>>>>>>>>>>>>>>>>>>>>\n12345",
				logFileDto.getText());
	}

	@Test
	public void skippingOneBuffer() throws Exception {
		FileUtils.write(file, "1234567890123456");
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(16, logFileDto.getOffset());
		assertEquals("12345\n<<<<<<<<<<<<<<<<<<<<< FILE TOO LONG, SKIPPING 5 chars >>>>>>>>>>>>>>>>>>>>>\n123456",
				logFileDto.getText());
	}

	@Test
	public void skippingTwoBuffers() throws Exception {
		FileUtils.write(file, "12345678901234567890");
		LogFileDto logFileDto = FileSystemLogUtils.readLogFileWithSizeLimit(file, 5);
		assertEquals(20, logFileDto.getOffset());
		assertEquals("12345\n<<<<<<<<<<<<<<<<<<<<< FILE TOO LONG, SKIPPING 10 chars >>>>>>>>>>>>>>>>>>>>>\n67890",
				logFileDto.getText());
	}

	private File newFile() throws IOException {
		File file = new File("target/", String.valueOf(System.currentTimeMillis()));
		while (file.exists()) {
			file = new File("target/", String.valueOf(System.currentTimeMillis()));
		}
		file.createNewFile();
		return file;
	}
}