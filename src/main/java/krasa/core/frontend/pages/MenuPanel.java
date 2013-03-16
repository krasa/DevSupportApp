package krasa.core.frontend.pages;

import java.util.logging.Logger;

import krasa.build.frontend.pages.BuildPage;
import krasa.merge.frontend.component.ProfileDropDownPanel;
import krasa.merge.frontend.pages.config.ConfigurationPage;
import krasa.merge.frontend.pages.config.ProfilesPage;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoPage;
import krasa.merge.frontend.pages.report.ReportPage;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public final class MenuPanel extends Panel {

	private static Logger logger = Logger.getLogger(MenuPanel.class.getName());

	// do li pridat class="active"
	// AttributeModifier modifier = new AttributeModifier("class", new Model("active"));
	public MenuPanel(String id, final BasePage parent) {
		super(id);
		// add(new BookmarkablePageLink("projects", HomePage.class));
		add(new BookmarkablePageLink("MergeInfo", MergeInfoPage.class));
		add(new BookmarkablePageLink("Build", BuildPage.class));
		add(new BookmarkablePageLink("ReportPage", ReportPage.class));
		add(new BookmarkablePageLink("ReleasesPage", ProfilesPage.class));
		//
		// add(new Link("categories") {
		//
		// @Override
		// public void onClick() {
		// CategoryAdminPanel categoryAdminPanel = new CategoryAdminPanel("current", parent);
		// parent.setCurrentPanelTo(categoryAdminPanel);
		// parent.setLeftPanel(new SvnProjectsLeftMenuPanel("left", parent, categoryAdminPanel));
		// }
		// });

		add(new BookmarkablePageLink("config", ConfigurationPage.class));
		add(new ProfileDropDownPanel("profile"));

	}
}
