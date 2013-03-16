package krasa.core.frontend.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class BasePanel extends Panel {
	public BasePanel(String id) {
		super(id);
		setOutputMarkupId(true);
	}

	public BasePanel(String id, IModel<?> model) {
		super(id, model);
		setOutputMarkupId(true);
	}
}
