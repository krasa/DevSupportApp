package krasa.core.frontend.commons;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class CheckBoxPanel<T> extends Panel {
	public abstract boolean isChecked(IModel<T> model);

	public CheckBoxPanel(String id, final IModel<T> model) {
		super(id, model);
		final CheckBox checkBox = new AjaxCheckBox("checkBox", new Model<>(CheckBoxPanel.this.isChecked(model))) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				CheckBoxPanel.this.onUpdate(target, getModel(), (IModel<T>) CheckBoxPanel.this.getDefaultModel());
			}
		};
		add(checkBox);
		setOutputMarkupId(true);
	}

	protected abstract void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel, IModel<T> model);

}
