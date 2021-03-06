package krasa.build.frontend.components;

import krasa.build.backend.domain.BuildJob;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.*;

public class PocessKillButton extends AjaxButton {
	private IModel<BuildJob> model;

	public PocessKillButton(String id, IModel<BuildJob> model) {
		super(id, new Model<>("Kill"));
		this.model = model;
	}

	@Override
	protected void onConfigure() {
		BuildJob object = model.getObject();
		if (object != null) {
			this.setEnabled(object.isProcessAlive());
		}
		super.onConfigure();
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		model.getObject().kill("killed manually");
		this.setEnabled(false);
		target.add(this);
	}
}
