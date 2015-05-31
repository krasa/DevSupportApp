package krasa.core.frontend.pages;

import java.io.*;

import org.apache.commons.io.FileUtils;

public class FileSystemLogUtils {

	public static File getLogFileByName(String logName) {
		if (!logName.endsWith(".log")) {
			logName = logName + ".log";
		}
		return new File("logs/" + logName);
	}

	public static String readFile(File logFile) {
		try {
			if (!logFile.exists()) {
				return "File does not exists: " + logFile.getAbsolutePath();
			}
			return FileUtils.readFileToString(logFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
