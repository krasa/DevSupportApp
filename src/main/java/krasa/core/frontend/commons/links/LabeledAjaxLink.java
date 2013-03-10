package krasa.core.frontend.commons.links;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;

/**
 * @author Vojtech Krasa
 */
public abstract class LabeledAjaxLink<T> extends AjaxLink<T> {

	private IModel<String> labelModel;

	public LabeledAjaxLink(final String id, final IModel<T> model, IModel<String> labelModel) {
		super(id, model);
		this.labelModel = labelModel;
	}

	@Override
	public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
		// Draw anything before the body?
		if (!isLinkEnabled() && getBeforeDisabledLink() != null) {
			getResponse().write(getBeforeDisabledLink());
		}

		// Render the body of the link
		replaceComponentTagBody(markupStream, openTag, labelModel.getObject());

		// Draw anything after the body?
		if (!isLinkEnabled() && getAfterDisabledLink() != null) {
			getResponse().write(getAfterDisabledLink());
		}
	}
}
