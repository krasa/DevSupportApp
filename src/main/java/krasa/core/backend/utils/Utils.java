package krasa.core.backend.utils;

import static org.apache.commons.lang3.StringUtils.*;

public class Utils {
	public static String toLogFormat(String line) {
		return substring(line, 0, 50).intern().replace("\n", "|") + "...."
				+ substring(line, line.length() - 50, line.length()).intern().replace("\n", "|");
	}
}
