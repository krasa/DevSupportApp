package krasa.build.frontend.components;

import java.io.Serializable;

import krasa.build.backend.dto.Result;

/**
 * @author Vojtech Krasa
 */
public abstract class LogModel implements Serializable {
	public abstract boolean isAlive();

	public abstract Result getLog();

	public abstract Result getNextLog(int length);

	public abstract boolean exists();
}
