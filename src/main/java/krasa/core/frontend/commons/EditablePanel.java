package krasa.core.frontend.commons;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class EditablePanel<T> extends Panel {

	private final TextField textfield;

	public EditablePanel(String id, IModel<T> rowModel, IModel textFieldModel) {
		super(id, rowModel);
		textfield = new TextField("textfield", textFieldModel);
		add(textfield);
	}

	public TextField getTextfield() {
		return textfield;
	}
}
