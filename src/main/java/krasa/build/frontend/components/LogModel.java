package krasa.build.frontend.components;

import java.io.Serializable;

import krasa.build.backend.dto.LogFileDto;

/**
 * @author Vojtech Krasa
 */
public abstract class LogModel implements Serializable {

	public abstract boolean isAlive();

	public abstract LogFileDto getLog();

	public abstract LogFileDto getNextLog(int offset);

	public abstract boolean exists();
}
