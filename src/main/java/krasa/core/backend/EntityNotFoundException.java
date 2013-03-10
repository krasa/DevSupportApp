package krasa.core.backend;

import java.io.Serializable;

public class EntityNotFoundException extends RuntimeException {
	private final Class<?> clazz;
	private final Serializable id;

	public EntityNotFoundException(Class<?> clazz, Serializable id) {
		this.clazz = clazz;
		this.id = id;
	}
}
