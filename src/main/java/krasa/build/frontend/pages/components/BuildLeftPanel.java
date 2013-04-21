package krasa.build.frontend.pages.components;

import java.util.List;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.commons.links.LabeledAjaxLink;
import krasa.core.frontend.components.BasePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class BuildLeftPanel extends BasePanel {
	private final IModel<Environment> actualEnvironment;
	@SpringBean
	private BuildFacade facade;

	public BuildLeftPanel(String id, IModel<Environment> actualEnvironment) {
		super(id);
		this.actualEnvironment = actualEnvironment;
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
				listItem.add(new LabeledAjaxLink<Environment>("name", listItem.getModel(), new PropertyModel<String>(
						listItem.getModel(), "name")) {
					@Override
					protected void onComponentTag(ComponentTag tag) {
						if (actualEnvironment != null) {
							Environment object = actualEnvironment.getObject();
							if (object != null) {
								if (getModel().getObject().getId().equals(object.getId())) {
									tag.getAttributes().put("class", "bold");
								}
							}
						}
						super.onComponentTag(tag);
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						setResponsePage(BuildPage.class, BuildPage.createPageParameters(getModelObject()));
					}
				});

			}
		};
		add(projectsList);
	}

}
