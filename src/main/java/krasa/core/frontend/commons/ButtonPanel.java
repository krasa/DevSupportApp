package krasa.core.frontend.commons;

import krasa.core.frontend.StaticImage;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ButtonPanel extends Panel {

	public ButtonPanel(String id, final String label) {
		super(id);
		addButton(label, null);
	}

	public ButtonPanel(String id, String label, StaticImage image) {
		super(id);
		addButton(label, image);
	}

	private void addButton(final String label, StaticImage image) {
		AjaxButton button = new AjaxButton("button") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(this);
				ButtonPanel.this.onSubmit(target, form);
			}
		};
		if (label != null) {
			button.add(new AttributeModifier("value", label));
		}
		if (image != null) {
			button.add(new AttributeModifier("src", image.getPath()));
			button.add(new AttributeModifier("type", "image"));
		}
		add(button);
	}

	protected abstract void onSubmit(AjaxRequestTarget target, Form<?> form);

}
