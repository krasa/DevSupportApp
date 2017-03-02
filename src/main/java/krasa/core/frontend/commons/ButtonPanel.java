package krasa.core.frontend.commons;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.core.frontend.StaticImage;
import krasa.core.frontend.pages.BasePage;

public abstract class ButtonPanel extends Panel {

	private static final Logger log = LoggerFactory.getLogger(ButtonPanel.class);
	public ButtonPanel(String id, String label) {
		super(id);
		addButton(label, null);
	}

	public ButtonPanel(String id, String label, StaticImage image) {
		super(id);
		addButton(label, image);
	}

	private void addButton(String label, StaticImage image) {
		AjaxButton button = new AjaxButton("button") {

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.add(this);
				try {
					ButtonPanel.this.onSubmit(target);
				} catch (Throwable e) {
					BasePage s = (BasePage) this.getPage();
					FeedbackPanel feedbackPanel = s.getFeedbackPanel();
					feedbackPanel.error(e.getMessage());
					target.add(feedbackPanel);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				super.onError(target);
			}
		};
		if (label != null) {
			button.add(new AttributeModifier("value", label));
		}
		if (image != null) {
			button.add(new AttributeModifier("src", image.getPath()));
			button.add(new AttributeModifier("type", "image"));
		}
		button.setDefaultFormProcessing(false);
		add(button);
	}

	protected abstract void onSubmit(AjaxRequestTarget target);

}
