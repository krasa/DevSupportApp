package krasa.core.frontend.commons.listeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

public abstract class ListEditor<T> extends RepeatingView implements IFormModelUpdateListener {
	List<T> items;

	public ListEditor(String id, IModel<List<T>> model) {
		super(id, model);
	}

	protected abstract void onPopulateItem(ListItem<T> item);

	public void addItem(T value) {
		items.add(value);
		ListItem<T> item = new ListItem<T>(newChildId(), items.size() - 1);
		add(item);
		onPopulateItem(item);
	}

	@Override
	protected void onBeforeRender() {
		if (!hasBeenRendered()) {
			items = new ArrayList<T>(getModelObject());
			for (int i = 0; i < items.size(); i++) {
				ListItem<T> li = new ListItem<T>(newChildId(), i);
				add(li);
				onPopulateItem(li);
			}
		}
		super.onBeforeRender();
	}

	public void updateModel() {
		setModelObject(items);
	}

	/**
	 * Indicates whether or not the item can be removed, usually by the use of {@link RemoveButton}
	 * 
	 * @param items
	 * @param item
	 * @return
	 */
	public boolean canRemove(List<T> items, T item) {
		return true;
	}

	@SuppressWarnings("unchecked")
	final boolean checkRemove(ListItem<?> item) {
		List<T> list = Collections.unmodifiableList(items);
		ListItem<T> li = (ListItem<T>) item;
		return canRemove(list, li.getModelObject());
	}

	/**
	 * Gets model
	 * 
	 * @return model
	 */
	@SuppressWarnings("unchecked")
	public final IModel<List<T>> getModel() {
		return (IModel<List<T>>) getDefaultModel();
	}

	/**
	 * Sets model
	 * 
	 * @param model
	 */
	public final void setModel(IModel<List<T>> model) {
		setDefaultModel(model);
	}

	/**
	 * Gets model object
	 * 
	 * @return model object
	 */
	@SuppressWarnings("unchecked")
	public final List<T> getModelObject() {
		return (List<T>) getDefaultModelObject();
	}

	/**
	 * Sets model object
	 * 
	 * @param object
	 */
	public final void setModelObject(List<T> object) {
		setDefaultModelObject(object);
	}

}
