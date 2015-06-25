package krasa.core.frontend.pages;

import java.io.*;
import java.util.Arrays;

import krasa.build.backend.dto.LogFileDto;

import org.apache.commons.io.*;
import org.slf4j.*;

public class FileSystemLogUtils {

	private static final Logger log = LoggerFactory.getLogger(FileSystemLogUtils.class);

	public static final int BUFFER_SIZE = 10000;

	public static File getLogFileByName(String logName) {
		if (!logName.endsWith(".log")) {
			logName = logName + ".log";
		}
		return new File("logs/" + logName);
	}

	public static LogFileDto readLogFileWithSizeLimit(File logFile, int bufferSize) {
		Reader reader = null;
		StringBuilder sb = new StringBuilder(bufferSize);

		int charactersSkipped = 0;
		int totalRead = 0;

		try {
			if (!logFile.exists()) {
				throw new FileNotFoundException("File does not exists: " + logFile.getAbsolutePath());
			}
			reader = new BufferedReader(new FileReader(logFile));
			// read first n chars
			char[] buffer = new char[bufferSize];
			int read = IOUtils.read(reader, buffer);
			totalRead = totalRead + read;
			sb.append(trimArray(buffer, read));

			// read last n chars or little bit more
			if (read == bufferSize) {
				char[] previousBuffer = null;
				int previouslyRead = 0;
				while (read != 0) {
					buffer = new char[bufferSize];
					read = IOUtils.read(reader, buffer);
					buffer = trimArray(buffer, read);
					totalRead = totalRead + read;

					if (read < bufferSize) {
						if (previousBuffer != null) {
							if (charactersSkipped > 0) {
								sb.append("\n").append("<<<<<<<<<<<<<<<<<<<<< FILE TOO LONG, SKIPPING ").append(
										charactersSkipped).append(" chars >>>>>>>>>>>>>>>>>>>>>").append("\n");
							}
							sb.append(previousBuffer);
						}
						sb.append(buffer);
						break;
					}
					if (previouslyRead != 0) {
						charactersSkipped = charactersSkipped + previouslyRead;
					}
					previousBuffer = buffer;
					previouslyRead = read;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

		}
		return new LogFileDto(totalRead, sb.toString());
	}

	private static char[] trimArray(char[] buffer, int read) {
		if (buffer.length != read) {
			return Arrays.copyOf(buffer, read);
		} else {
			return buffer;
		}
	}

	public static String readFromEnd(File file, int bytesToReadFromEnd) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
			long length = randomAccessFile.length();
			int max = (int) Math.max(length - bytesToReadFromEnd, 0);
			randomAccessFile.seek(max);
			byte[] b = new byte[(int) (length - max)];
			randomAccessFile.read(b);
			return new String(b, Charsets.UTF_8);
		} catch (FileNotFoundException e) {
			log.error("", e);
			return "";
		} catch (IOException e) {
			log.error("", e);
			return "";
		}
	}
}
