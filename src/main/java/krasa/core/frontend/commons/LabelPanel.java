package krasa.core.frontend.commons;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class LabelPanel<T> extends Panel {

	public static final String ID = "text";

	public LabelPanel(String id, IModel<String> labelModel) {
		super(id);
		add(getComponent(ID, labelModel));
	}

	protected abstract Component getComponent(String id, IModel<String> labelModel);
}
