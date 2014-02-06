package krasa.core.backend.utils;

import java.io.OutputStream;

import org.slf4j.Logger;

/**
 * @author Vojtech Krasa
 */
public class LogOutputStream extends OutputStream {

	private Logger logger;

	private String mem;

	public LogOutputStream(Logger logger) {
		setLogger(logger);
		mem = "";
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void write(int b) {
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (b & 0xff);
		mem = mem + new String(bytes);

		if (mem.endsWith("\n")) {
			mem = mem.substring(0, mem.length() - 1);
			flush();
		}
	}

	public void flush() {
		logger.info(mem);
		mem = "";
	}
}
