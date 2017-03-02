package krasa.core.frontend.commons.listeditor;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class ListItem<T> extends Item<T> {
	public ListItem(String id, int index) {
		super(id, index);
		setModel(new ListItemModel());
	}

	private class ListItemModel implements IModel<T> {
		@SuppressWarnings("unchecked")
		@Override
		public T getObject() {
			return ((ListEditor<T>) ListItem.this.getParent()).items.get(getIndex());
		}
		
	}
}
