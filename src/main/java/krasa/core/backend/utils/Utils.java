package krasa.core.backend.utils;

import static org.apache.commons.lang3.StringUtils.*;

public class Utils {
	public static String toLogFormat(String line) {
		return substring(line, 0, 100).intern().replace("\n", "|");
	}
}
