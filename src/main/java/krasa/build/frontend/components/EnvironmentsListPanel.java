package krasa.build.frontend.components;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.build.backend.domain.Environment;

public class EnvironmentsListPanel extends Panel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected ListView<Environment> list;

	public EnvironmentsListPanel(String id, IModel<List<Environment>> environmentsModel) {
		super(id);
		setOutputMarkupId(true);
		createList(environmentsModel);
	}

	private void createList(IModel<List<Environment>> environmentsModel) {
		list = new ListView<Environment>("environment", environmentsModel) {

			@Override
			protected void populateItem(ListItem<Environment> item) {
				item.setOutputMarkupId(true); // write id attribute of element to html
				Environment environment = item.getModelObject();
				item.setMarkupId("envId" + environment.getId());
				item.add(new EnvironmentDetailPanel("detail", item.getModel()));
			}

		};
		list.setOutputMarkupId(true);
		add(list);
	}

}
