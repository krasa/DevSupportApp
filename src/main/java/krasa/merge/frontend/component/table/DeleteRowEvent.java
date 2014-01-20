package krasa.merge.frontend.component.table;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Vojtech Krasa
 */
public class DeleteRowEvent<T> {
	private AjaxRequestTarget target;
	private T object;

	public DeleteRowEvent(T object) {
		this.object = object;
	}

	public AjaxRequestTarget getTarget() {
		return target;
	}

	public void setTarget(AjaxRequestTarget target) {
		this.target = target;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}
