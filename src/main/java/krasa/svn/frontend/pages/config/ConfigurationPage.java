package krasa.svn.frontend.pages.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.ShutdownManager;
import krasa.core.frontend.commons.table.PropertyEditableColumn;
import krasa.core.frontend.pages.BasePage;
import krasa.svn.backend.domain.Profile;
import krasa.svn.backend.facade.SvnFacade;
import krasa.svn.backend.service.SvnFolderRefreshFacade;

/**
 * @author Vojtech Krasa
 */
public class ConfigurationPage extends BasePage {

	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	@SpringBean
	private SvnFacade facade;
	@SpringBean
	private BuildFacade buildFacade;
	@SpringBean
	private ShutdownManager shutdownManager;

	public ConfigurationPage() {
		initComponents();
	}

	public ConfigurationPage(PageParameters parameters) {
		super(parameters);
		initComponents();
	}

	private void initComponents() {
		queue(new RepositoryConfigurationPanel("repositories"));
		final Form<Profile> form = new Form<>("form");

		form.add(new RefreshBranchesButton(form, "refreshBranchesButton"));
		form.add(new IndicatingAjaxButton("refreshReleasesFormSvn") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
				facade.refreshReleasesFromSvn();
				ajaxRequestTarget.add(form);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget) {
				error("Error");
			}
		});
		form.add(new IndicatingAjaxButton("clearSvn") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
				facade.deleteAllSvnBranches();
				ajaxRequestTarget.add(form);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget) {
				error("Error");
			}
		});

		form.add(new IndicatingAjaxButton("cleanHsqldb") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
				int i = buildFacade.cleanHsqldb();
				info("deleted " + i);
				ajaxRequestTarget.add(getFeedbackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget) {
				error("Error");
				ajaxRequestTarget.add(getFeedbackPanel());
			}
		});
		form.add(new Button("shutdown") {

			@Override
			public void onSubmit() {
				super.onSubmit();
				shutdownManager.initiateShutdown(0);
				throw new RedirectToUrlException("/monitoring/health", HttpServletResponse.SC_MOVED_PERMANENTLY);
			}

		});
		ArrayList<IColumn<User, String>> cols = new ArrayList<>();
		cols.add(
				new PropertyEditableColumn<>(new Model<>("host name"), "hostName", "hostName", 200));
		cols.add(
				new PropertyEditableColumn<>(new Model<>("user name"), "userName", "userName", 200));

		ArrayList<User> users = new ArrayList<>();
		LoadableDetachableModel<List<User>> loadableDetachableModel = new LoadableDetachableModel<List<User>>() {

			@Override
			protected List<User> load() {
				return users;
			}
		};
		SortableDataProvider<User, String> sortableDataProvider = new SortableDataProvider<User, String>() {

			@Override
			public Iterator<? extends User> iterator(long l, long l1) {
				List<User> object = loadableDetachableModel.getObject();
				return object.iterator();
			}

			@Override
			public long size() {
				return loadableDetachableModel.getObject().size();
			}

			@Override
			public IModel<User> model(User user) {
				return new Model<>(user);
			}
		};
		AjaxFallbackDefaultDataTable<User, String> users1 = new AjaxFallbackDefaultDataTable<>("users", cols, sortableDataProvider, Integer.MAX_VALUE);
		form.add(users1);
		form.add(new AjaxButton("addUser") {

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				users.add(new User("host", "name"));
			}
		});
		setOutputMarkupPlaceholderTag(true);

		queue(form);
	}

	@Override
	protected Component newCurrentPanel(String id) {
		return new EmptyPanel(id);
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new EmptyPanel(id);
	}

	public static class RefreshBranchesButton extends IndicatingAjaxButton {

		@SpringBean
		private SvnFolderRefreshFacade svnFolderResfreshService;

		private final Form form;

		public RefreshBranchesButton(Form form, String id) {
			super(id);
			this.form = form;
			setDefaultFormProcessing(false);
		}

		@Override
		protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
			svnFolderResfreshService.reloadProjects();
			svnFolderResfreshService.refreshAllProjects();
			ajaxRequestTarget.add(form);
			info("Done");
		}

		@Override
		protected void onError(AjaxRequestTarget ajaxRequestTarget) {
			error("Error");
		}
	}

}
