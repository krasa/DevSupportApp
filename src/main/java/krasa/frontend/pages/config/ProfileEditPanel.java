package krasa.frontend.pages.config;

import krasa.backend.domain.Profile;
import krasa.backend.facade.Facade;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.logging.Logger;

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
                setResponsePage(ConfigurationPage.class, ConfigurationPage.createPageParameters(getModelObject()));
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
