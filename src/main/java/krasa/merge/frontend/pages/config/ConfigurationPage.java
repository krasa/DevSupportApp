package krasa.merge.frontend.pages.config;

import java.util.logging.Logger;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;
import krasa.merge.backend.service.SvnFolderRefreshService;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
	@SpringBean
	private SvnFolderRefreshService svnFolderResfreshService;
	protected IndicatingAjaxButton components;

	public ConfigurationPage() {
		initComponents();
	}

	public ConfigurationPage(PageParameters parameters) {
		super(parameters);
		initComponents();
	}

	private MarkupContainer initComponents() {
		add(new RepositoryConfigurationPanel("repositories"));
		final Form<Profile> form = new Form<Profile>("form");

		form.add(new IndicatingAjaxButton("refreshProjectsAndBranches") {
			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				svnFolderResfreshService.reloadProjects();
				svnFolderResfreshService.refreshAllProjects();
				ajaxRequestTarget.add(form);
				info("Processing");
				// StatusLabel status1 = new StatusLabel("status", svnRefreshCallbackIModel);
				// status.replaceWith(status1);
				// status = status1;
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});
		components = new IndicatingAjaxButton("refreshReleasesFormSvn") {
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
		};
		components.setOutputMarkupId(true);
		form.add(components);

		// form.add(new TagItTextField<String>("tagit", Model.of("")) {
		//
		// @Override
		// protected Iterable<String> getChoices(String input) {
		// return facade.getSelectedBranchesName(input);
		// }
		// });
		setOutputMarkupPlaceholderTag(true);

		return add(form);
	}

	@Override
	protected Component newCurrentPanel(String id) {
		return new EmptyPanel(id);
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new EmptyPanel(id);
	}
}
