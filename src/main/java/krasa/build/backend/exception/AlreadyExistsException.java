package krasa.build.backend.exception;

/**
 * @author Vojtech Krasa
 */
public class AlreadyExistsException extends Exception {
	private String name;

	public AlreadyExistsException(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Already exists";
	}
}
