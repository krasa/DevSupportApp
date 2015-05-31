package krasa.svn.frontend.pages.config;

import java.util.logging.Logger;

import krasa.core.frontend.MySession;
import krasa.svn.backend.domain.Profile;
import krasa.svn.backend.facade.SvnFacade;

import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Display settings for project
 */
public final class ProfileEditPanel extends Panel {

	@SpringBean
	private SvnFacade facade;
	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	public ProfileEditPanel(String current, IModel<Profile> model) {
		super(current, model);
		initComponents(model);
	}

	private void initComponents(IModel<Profile> model) {
		Form<Profile> form = new Form<Profile>("form", new CompoundPropertyModel<>(model)) {
		};
		form.add(new TextField<String>("name"));
		form.add(new Button("save") {

			@Override
			public void onSubmit() {
				Profile modelObject = (Profile) getForm().getModelObject();
				facade.updateProfile(modelObject);
				setResponsePage(ProfilesPage.class, ProfilesPage.createPageParameters(modelObject));
			}
		});
		form.add(new Button("delete") {

			@Override
			public void onSubmit() {
				facade.deleteProfile((Profile) getForm().getModelObject());
				Profile defaultProfile = facade.getDefaultProfile();
				MySession.get().setCurrentProfile(defaultProfile.getId());
				setResponsePage(ProfilesPage.class, ProfilesPage.createPageParameters(defaultProfile));
			}
		});
		add(form);

	}

}
