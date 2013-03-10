package krasa.core.frontend.commons;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class EditablePanel extends Panel {

	public EditablePanel(String id, IModel model) {
		super(id);
		add(new TextField("textfield", model));
	}
}
