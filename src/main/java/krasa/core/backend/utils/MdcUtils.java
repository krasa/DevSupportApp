package krasa.core.backend.utils;

import org.jboss.logging.MDC;
import org.slf4j.*;

import ch.qos.logback.classic.ClassicConstants;

/**
 * @author Vojtech Krasa
 */
public class MdcUtils {

	private static final Logger log = LoggerFactory.getLogger(MdcUtils.class);

	public static final String JOG_NAME = "logName";
	static Marker SIFT_END = MarkerFactory.getMarker("SIFT_END");
	static {
		SIFT_END.add(ClassicConstants.FINALIZE_SESSION_MARKER);
	}

	public static void removeLogName() {
		log.info(SIFT_END, "file end");
		MDC.remove(JOG_NAME);
	}

	public static Object putLogName(String val) {
		return MDC.put(JOG_NAME, val);
	}
}
