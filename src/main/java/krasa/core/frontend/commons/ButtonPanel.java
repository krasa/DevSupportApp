package krasa.core.frontend.commons;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ButtonPanel extends Panel {

	public ButtonPanel(String id, final String label) {
		super(id);
		add(new AjaxButton("button") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ButtonPanel.this.onSubmit(target, form);
			}

			@Override
			public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
				replaceComponentTagBody(markupStream, openTag, label);

			}
		});
	}

	protected abstract void onSubmit(AjaxRequestTarget target, Form<?> form);

}
