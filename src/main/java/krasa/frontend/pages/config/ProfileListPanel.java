package krasa.frontend.pages.config;

import krasa.backend.domain.Profile;
import krasa.backend.facade.Facade;
import krasa.frontend.pages.commons.links.LabeledAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;


public class ProfileListPanel extends Panel {
    @SpringBean
    private Facade facade;

    public ProfileListPanel(String id) {
        super(id);
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
                listItem.add(new LabeledAjaxLink<Profile>("name", listItem.getModel(), new PropertyModel<String>(listItem.getModel(), "name")) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(ConfigurationPage.class, ConfigurationPage.createPageParameters(getModelObject()));
                    }
                });

            }
        };
        add(projectsList);
    }

}
