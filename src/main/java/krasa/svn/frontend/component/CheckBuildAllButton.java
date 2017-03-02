package krasa.svn.frontend.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;

public abstract class CheckBuildAllButton extends AjaxButton {

	private final Form form;

	public CheckBuildAllButton(Form form, String id) {
		super(id);
		this.form = form;
		setDefaultFormProcessing(false);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
		buildAll(ajaxRequestTarget);
		info("Done");
	}

	protected abstract void buildAll(AjaxRequestTarget ajaxRequestTarget);

	@Override
	protected void onError(AjaxRequestTarget ajaxRequestTarget) {
		error("Error");
	}
}
