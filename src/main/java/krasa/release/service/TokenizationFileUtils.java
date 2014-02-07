package krasa.release.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TokenizationFileUtils {

	public static File getTemplateFile() {
		return new File("tokenizationTemplate.json");
	}

	public static void rewriteTemplate(String json1) throws IOException {
		File file = getTemplateFile();
		FileUtils.writeStringToFile(file, json1);
	}

	public static String readTemplate() {
		try {
			File file = getTemplateFile();
			if (file.exists()) {
				return FileUtils.readFileToString(file);
			} else {
				throw new IllegalStateException("file does not exists: " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File getLogFileByName(String logName) {
		if (!logName.endsWith(".log")) {
			logName = logName + ".log";
		}
		return new File("logs/" + logName);
	}
}
