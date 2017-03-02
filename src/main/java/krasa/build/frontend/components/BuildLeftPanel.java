package krasa.build.frontend.components;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.core.frontend.components.BasePanel;

public class BuildLeftPanel extends BasePanel {

	@SpringBean
	private BuildFacade facade;

	public BuildLeftPanel(String id) {
		super(id);
		add(new CurrentlyBuildingLeftPanel("currentlyBuilding"));
		add(new LastBuildsLeftPanel("lastBuilds"));
		initList();
	}

	private void initList() {
		ListView<Environment> projectsList;
		LoadableDetachableModel<List<Environment>> model = new LoadableDetachableModel<List<Environment>>() {

			@Override
			protected List<Environment> load() {
				return facade.getEnvironments();
			}
		};

		projectsList = new ListView<Environment>("item", model) {

			@Override
			protected void populateItem(ListItem<Environment> listItem) {
				PageParameters pageParameters = BuildPage.createPageParameters(listItem.getModelObject().getName());
				PropertyModel<String> labelModel = new PropertyModel<>(listItem.getModel(), "name");
				listItem.add(new LabeledBookmarkablePageLink("name", BuildPage.class, pageParameters, labelModel));
			}
		};
		add(projectsList);
	}
}
