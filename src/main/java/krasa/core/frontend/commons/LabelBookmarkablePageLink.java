package krasa.core.frontend.commons;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LabelBookmarkablePageLink extends BookmarkablePageLink {

	private String label;

	public LabelBookmarkablePageLink(String id, Class pageClass, PageParameters parameters, IModel<String> labelModel) {
		super(id, pageClass, parameters);
		this.label = labelModel.getObject();
	}

	public LabelBookmarkablePageLink(String id, Class pageClass, IModel<String> labelModel) {
		super(id, pageClass);
		this.label = labelModel.getObject();
	}

	public LabelBookmarkablePageLink(String id, Class pageClass, String label) {
		super(id, pageClass);
		this.label = label;
	}

	public LabelBookmarkablePageLink(String id, Class pageClass, PageParameters parameters, String label) {
		super(id, pageClass);
		this.label = label;
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag, label);
	}
}
