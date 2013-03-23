package krasa.core.frontend.commons;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ProjectLinkPanel extends Panel {

	public ProjectLinkPanel(String id, IModel<String> labelModel, IModel<String> linkParameterModel) {
		super(id);
		add(new ProjectLink("link", labelModel, linkParameterModel));

	}
}
