package krasa.merge.frontend.pages.config;

import java.util.logging.Logger;

import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Display settings for project
 */
public final class ProfileEditPanel extends Panel {

	@SpringBean
	private Facade facade;
	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	private void initComponents(IModel<Profile> model) {
		Form<Profile> form = new Form<Profile>("form", new CompoundPropertyModel<Profile>(model)) {
			@Override
			protected void onSubmit() {
				facade.updateProfile(getModelObject());
				setResponsePage(ProfilesPage.class, ProfilesPage.createPageParameters(getModelObject()));
			}
		};
		form.add(new TextField<String>("name"));

		add(form);

	}

	public ProfileEditPanel(String current, IModel<Profile> model) {
		super(current, model);
		initComponents(model);
	}

}
