package krasa.build.frontend.components;

import krasa.build.backend.execution.adapter.ProcessAdapter;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class PocessKillButton extends AjaxButton {
	private IModel<ProcessAdapter> model;

	public PocessKillButton(String id, IModel<ProcessAdapter> model) {
		super(id, new Model<String>("Kill"));
		this.model = model;
	}

	@Override
	protected void onConfigure() {
		ProcessAdapter object = model.getObject();
		if (object != null) {
			this.setEnabled(object.isAlive());
		}
		super.onConfigure();
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		model.getObject().kill();
		this.setEnabled(false);
		target.add(this);
		super.onSubmit();
	}
}