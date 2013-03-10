package krasa.core.frontend.commons.listeditor;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;

public abstract class EditorButton extends Button {
	private transient ListItem<?> parent;

	public EditorButton(String id) {
		super(id);
	}

	protected final ListItem<?> getItem() {
		if (parent == null) {
			parent = findParent(ListItem.class);
		}
		return parent;
	}

	protected final List<?> getList() {
		return getEditor().items;
	}

	protected final ListEditor<?> getEditor() {
		return (ListEditor<?>) getItem().getParent();
	}

	@Override
	protected void onDetach() {
		parent = null;
		super.onDetach();
	}

}
