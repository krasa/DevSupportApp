package krasa.core.frontend.components;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class BaseFormPanel<T> extends BasePanel {

	protected Form form;

	public BaseFormPanel(String id) {
		super(id);
		init();
		initForm(form);
	}

	public BaseFormPanel(String id, IModel<T> model) {
		super(id, model);
		init();
		initForm(form);
	}

	protected abstract void initForm(Form form);

	public IModel<T> getFormPanelModel() {
		return (IModel<T>) getDefaultModel();
	}

	private MarkupContainer init() {
		form = new Form("form");
		return add(form);
	}

}
