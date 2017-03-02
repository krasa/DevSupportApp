package krasa.core.frontend.pages;

import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.env.Environment;

import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.web.CookieUtils;
import krasa.intellij.IntelliJMainPage;
import krasa.overnight.OvernightResultsPage;
import krasa.release.frontend.TokenizationPage;
import krasa.smrt.SmrtMainPage;
import krasa.svn.frontend.component.ProfileDropDownPanel;
import krasa.svn.frontend.pages.config.ConfigurationPage;
import krasa.svn.frontend.pages.config.ProfilesPage;
import krasa.svn.frontend.pages.config.TestPage;
import krasa.svn.frontend.pages.mergeinfo.MergeInfoPage;
import krasa.svn.frontend.pages.report.ReportPage;

public final class MainMenuPanel extends Panel {

	private static Logger logger = Logger.getLogger(MainMenuPanel.class.getName());
	@SpringBean
	Environment environment;

	private org.apache.wicket.model.IModel<String> userNameModel = new LoadableDetachableModel<String>() {

		@Override
		protected String load() {
			return CookieUtils.getCookie_userName();
		}
	};

	// do li pridat class="active"
	// AttributeModifier modifier = new AttributeModifier("class", new Model("active"));
	public MainMenuPanel(String id, BasePage parent) {
		super(id);
		// add(new BookmarkablePageLink("projects", HomePage.class));
		add(new BookmarkablePageLink("Merge", MergeInfoPage.class));
		add(new BookmarkablePageLink("Build", BuildPage.class));
		add(new BookmarkablePageLink("Tokenization", TokenizationPage.class));
		add(new BookmarkablePageLink("Overnights", OvernightResultsPage.class));
		add(new BookmarkablePageLink("ReportPage", ReportPage.class));
		add(new BookmarkablePageLink("ReleasesPage", ProfilesPage.class));
		add(new BookmarkablePageLink("config", ConfigurationPage.class));
		add(new BookmarkablePageLink("intellij", IntelliJMainPage.class));
		add(new BookmarkablePageLink("Smrt", SmrtMainPage.class));
		add(new BookmarkablePageLink("Test", TestPage.class));
		add(new Label("getActiveProfiles", new LoadableDetachableModel<String>() {

			@Override
			protected String load() {
				return Arrays.toString(environment.getActiveProfiles());
			}
		}));
		add(new ProfileDropDownPanel("profile"));
		TextField<String> userName = new TextField<>("userName", userNameModel);
		userName.add(OnChangeAjaxBehavior.onChange(this::setUserNameToCookie));
		Form<Object> form = new Form<>("form");
		add(form);
		form.add(userName);
	}

	public void setUserNameToCookie(AjaxRequestTarget userName) {
		CookieUtils.newCookie_userName(userNameModel.getObject());

	}

}
