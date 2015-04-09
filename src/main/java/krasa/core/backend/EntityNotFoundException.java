package krasa.core.backend;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EntityNotFoundException extends RuntimeException {

	private final Class<?> clazz;
	private final Serializable id;

	public EntityNotFoundException(Class<?> clazz, Serializable id) {
		this.clazz = clazz;
		this.id = id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("clazz", clazz).append("id", id).toString();
	}
}
