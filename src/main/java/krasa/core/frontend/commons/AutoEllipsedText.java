package krasa.core.frontend.commons;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class AutoEllipsedText extends Panel {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	/**
	 * If the text is smaller than the maximum length, truncate it
	 */
	private int maximumLength = 3 * 80;

	/**
	 * Truncate it at (or around) this character - less than maximumLength to prevent only chopping off a few
	 * characters.
	 */
	private int truncatePoint = maximumLength - 20;

	public AutoEllipsedText(String id) {
		super(id);

		Label initialText = new Label("initialText", new PropertyModel<String>(this, "initialText"));
		initialText.setOutputMarkupId(true);
		add(initialText);

		Label fullText = new Label("description", getDefaultModel());
		fullText.setOutputMarkupId(true);
		add(fullText);

		WebMarkupContainer showMore = new WebMarkupContainer("showMore") {

			/**
             *
             */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.wicket.Component#isVisible()
			 */

			@Override
			public boolean isVisible() {
				String text = AutoEllipsedText.this.getDefaultModelObjectAsString();
				return text.length() > maximumLength;
			}

		};
		showMore.setOutputMarkupId(true);
		showMore.add(new AttributeModifier("onclick", true, new Model<String>("document.getElementById('"
				+ initialText.getMarkupId() + "').style.display = 'none'; document.getElementById('"
				+ fullText.getMarkupId() + "').style.display = 'block'; document.getElementById('"
				+ showMore.getMarkupId() + "').style.display = 'none'")));
		add(showMore);
	}

	public String getInitialText() {
		String fullText = getDefaultModelObjectAsString();
		if (fullText.length() > maximumLength) {
			return fullText.substring(0, truncatePoint);
		} else {
			return fullText;
		}
	}
}
