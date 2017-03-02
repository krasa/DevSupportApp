package krasa.core.frontend.commons;

import krasa.core.frontend.utils.Strings;

import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

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
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		List<String> listOfStrings = splitEqually(getDefaultModelObjectAsString(),15000);
		for(String onePart : listOfStrings) {
			CharSequence body = Strings.toMultilineMarkup(onePart);
			replaceComponentTagBody(markupStream, openTag, body);
		}
	}

	private List<String> splitEqually(String text, int size) {
		List<String> ret = new ArrayList<>((text.length() + size - 1) / size);
		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}
}
