package krasa.overnight;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class OvernightDbModel<T extends AbstractEntity> implements IModel<T> {

	private final Integer id;
	private final Class clazz;
	@SpringBean
	OvernightFacade overnightFacade;

	private T object;

	public OvernightDbModel(T object) {
		this.object = object;
		id = object.getId();
		clazz = object.getClass();

	}

	@Override
	public T getObject() {
		if (object == null) {
			object = (T) overnightFacade.findById(clazz, id);
		}
		return object;
	}

	@Override
	public void setObject(T object) {

	}

	@Override
	public void detach() {
		object = null;
	}
}
