package krasa.core.backend.utils;

import org.jboss.logging.MDC;

/**
 * @author Vojtech Krasa
 */
public class MdcUtils {
	public static final String JOG_NAME = "logName";

	public static void removeLogName() {
		MDC.remove(JOG_NAME);
	}

	public static Object putLogName(final String val) {
		return MDC.put(JOG_NAME, val);
	}
}
