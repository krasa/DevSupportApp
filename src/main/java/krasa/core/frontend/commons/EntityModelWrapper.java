package krasa.core.frontend.commons;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.wicket.model.IModel;

public class EntityModelWrapper<T extends AbstractEntity> implements IModel<T> {
	private static final long serialVersionUID = -3134349680682543348L;
	private Integer id;
	private IModel<T> wrappedModel;

	public EntityModelWrapper(IModel<T> wrappedModel) {
		this.wrappedModel = wrappedModel;
		id = wrappedModel.getObject().getId();
	}

	public EntityModelWrapper() {

	}

	@Override
	public T getObject() {
		return wrappedModel.getObject();
	}

	@Override
	public void detach() {
		wrappedModel.detach();
	}

	public void setWrappedModel(IModel<T> wrappedModel) {
		this.wrappedModel = wrappedModel;
		id = wrappedModel.getObject().getId();
	}

	@Override
	public void setObject(AbstractEntity object) {
		throw new UnsupportedOperationException(getClass() + " does not support #setObject(T entity)");
	}

	public Integer getId() {
		return id;
	}
}
