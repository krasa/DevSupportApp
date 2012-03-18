package krasa.frontend.pages.svn;

import krasa.backend.domain.SvnFolder;
import krasa.backend.facade.Facade;
import krasa.frontend.pages.commons.ProjectLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;


public class SvnProjectsLeftMenuPanel extends Panel {

    @SpringBean
    private Facade facade;

    public SvnProjectsLeftMenuPanel(String id) {
        super(id);
        initProjectList();
    }


    private void initProjectList() {
        ListView<SvnFolder> projectsList;
        LoadableDetachableModel<List<SvnFolder>> loadableDetachableModel = new LoadableDetachableModel<List<SvnFolder>>() {
            @Override
            protected List<SvnFolder> load() {
                return facade.getProjects();
            }
        };

        projectsList = new ListView<SvnFolder>("project", loadableDetachableModel) {
            @Override
            protected void populateItem(ListItem<SvnFolder> listItem) {
                listItem.add(new ProjectLink("name", listItem.getModel()));

            }
        };
        add(projectsList);
    }

}
