package krasa.core.frontend.components;

import org.apache.wicket.markup.html.panel.Panel;

public class BaseEmptyPanel extends Panel {

	public BaseEmptyPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}
}
