package krasa.svn.frontend.pages.config;

import java.util.List;

import krasa.core.frontend.commons.links.LabeledAjaxLink;
import krasa.svn.backend.domain.Profile;

import krasa.svn.backend.facade.SvnFacade;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ProfileListPanel extends Panel {
	private final IModel<Profile> actualProfile;
	@SpringBean
	private SvnFacade facade;

	public ProfileListPanel(String id, IModel<Profile> actualProfile) {
		super(id);
		this.actualProfile = actualProfile;
		initList();
	}

	private void initList() {
		ListView<Profile> projectsList;
		LoadableDetachableModel<List<Profile>> model = new LoadableDetachableModel<List<Profile>>() {
			@Override
			protected List<Profile> load() {
				return facade.getProfiles();
			}
		};

		projectsList = new ListView<Profile>("profile", model) {
			@Override
			protected void populateItem(ListItem<Profile> listItem) {
				listItem.add(new LabeledAjaxLink<Profile>("name", listItem.getModel(), new PropertyModel<String>(
						listItem.getModel(), "name")) {
					@Override
					protected void onComponentTag(ComponentTag tag) {
						if (getModel().getObject().getId().equals(actualProfile.getObject().getId())) {
							tag.getAttributes().put("class", "bold");
						}
						super.onComponentTag(tag);
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						setResponsePage(ProfilesPage.class, ProfilesPage.createPageParameters(getModelObject()));
					}
				});

			}
		};
		add(projectsList);
	}

}
