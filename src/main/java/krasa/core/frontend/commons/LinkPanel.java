package krasa.core.frontend.commons;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class LinkPanel<T> extends Panel {

	public static final String LINK = "link";

	public LinkPanel(String id, IModel<String> labelModel, IModel<T> linkParameterModel) {
		super(id);
		add(getComponent(LINK, labelModel, linkParameterModel));
	}

	protected abstract Link getComponent(String id, IModel<String> labelModel, IModel<T> linkParameterModel);
}
