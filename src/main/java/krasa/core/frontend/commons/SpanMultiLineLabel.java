package krasa.core.frontend.commons;

import krasa.core.frontend.utils.Strings;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;

public class SpanMultiLineLabel extends MultiLineLabel {

	public SpanMultiLineLabel(String id) {
		super(id);
	}

	public SpanMultiLineLabel(String id, String label) {
		super(id, label);
	}

	public SpanMultiLineLabel(String id, IModel<?> model) {
		super(id, model);
	}

	@Override
	public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
		CharSequence body = Strings.toMultilineMarkup(getDefaultModelObjectAsString());
		replaceComponentTagBody(markupStream, openTag, body);
	}

}
