package krasa.merge.frontend.pages.config;

import java.util.logging.Logger;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;
import krasa.merge.backend.service.SvnFolderRefreshService;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class ConfigurationPage extends BasePage {

	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	@SpringBean
	private Facade facade;

	public ConfigurationPage() {
		initComponents();
	}

	public ConfigurationPage(PageParameters parameters) {
		super(parameters);
		initComponents();
	}

	private void initComponents() {
		add(new RepositoryConfigurationPanel("repositories"));
		final Form<Profile> form = new Form<>("form");

		form.add(new RefreshBranchesButton(form, "refreshBranchesButton"));
		form.add(new IndicatingAjaxButton("refreshReleasesFormSvn") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				facade.refreshReleasesFromSvn();
				ajaxRequestTarget.add(form);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});
		form.add(new IndicatingAjaxButton("clearSvn") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				facade.deleteAllSvnBranches();
				ajaxRequestTarget.add(form);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});

		form.add(new IndicatingAjaxButton("cleanHsqldb") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				facade.cleanHsqldb();
				info("done");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});

		setOutputMarkupPlaceholderTag(true);

		add(form);
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
		private SvnFolderRefreshService svnFolderResfreshService;

		private final Form form;

		public RefreshBranchesButton(Form form, String id) {
			super(id);
			this.form = form;
			setDefaultFormProcessing(false);
		}

		@Override
		protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
			svnFolderResfreshService.reloadProjects();
			svnFolderResfreshService.refreshAllProjects();
			ajaxRequestTarget.add(form);
			info("Done");
		}

		@Override
		protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
			error("Error");
		}
	}

}
