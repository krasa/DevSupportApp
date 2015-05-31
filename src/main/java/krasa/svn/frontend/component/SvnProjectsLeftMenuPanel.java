package krasa.svn.frontend.component;

import java.util.List;

import krasa.core.frontend.commons.ProjectLink;
import krasa.svn.backend.domain.SvnFolder;
import krasa.svn.backend.facade.SvnFacade;

import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SvnProjectsLeftMenuPanel extends Panel {

	@SpringBean
	private SvnFacade facade;

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
				listItem.add(new ProjectLink("name", new PropertyModel<String>(listItem.getModel(), "name"),
						new PropertyModel<String>(listItem.getModel(), "path")));
			}
		};
		add(projectsList);
	}

}
