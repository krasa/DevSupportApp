package krasa.core.frontend.commons;

import java.io.Serializable;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.wicket.model.IModel;

public abstract class AbstractEntityModel<T extends AbstractEntity<?>> implements IModel<T> {
	private static final long serialVersionUID = -3134349680682543348L;
	private final Class<?> clazz;
	private Serializable id;

	private T entity;

	public AbstractEntityModel(T entity) {
		clazz = entity.getClass();
		id = entity.getId();
		this.entity = entity;
	}

	public AbstractEntityModel(Class<? extends T> clazz, Serializable id) {
		this.clazz = clazz;
		this.id = id;
	}

	@Override
	public T getObject() {
		if (entity == null) {
			if (id != null) {
				entity = load(clazz, id);
				if (entity == null) {
					throw new krasa.core.backend.EntityNotFoundException(clazz, id);
				}
			}
		}
		return entity;
	}

	@Override
	public void detach() {
		if (entity != null) {
			if (entity.getId() != null) {
				id = entity.getId();
				entity = null;
			}
		}
	}

	protected abstract T load(Class<?> clazz, Serializable id);

	@Override
	public void setObject(T object) {
		throw new UnsupportedOperationException(getClass() + " does not support #setObject(T entity)");
	}

	public Serializable getId() {
		return id;
	}
}
