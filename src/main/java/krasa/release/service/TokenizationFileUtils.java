package krasa.release.service;

import java.io.File;

public class TokenizationFileUtils {

	public static File getLogFileByName(String logName) {
		if (!logName.endsWith(".log")) {
			logName = logName + ".log";
		}
		return new File("logs/" + logName);
	}
}
