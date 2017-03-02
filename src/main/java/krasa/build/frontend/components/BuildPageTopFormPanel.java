package krasa.build.frontend.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.components.BasePanel;

public class BuildPageTopFormPanel extends BasePanel {

	@SpringBean
	private BuildFacade facade;

	private String environmentName;

	public BuildPageTopFormPanel(String id) {
		super(id);
		add(createForm());
	}

	private Form createForm() {
		Form form = new Form("createEnvironmentForm");
		TextField<String> name = new TextField<>("name", new PropertyModel<String>(this, "environmentName"));
		name.setRequired(true);
		form.add(name);
		form.add(new AddEnvironmentButton());
		form.add(new KillAllBuildsButton());
		return form;
	}

	private class AddEnvironmentButton extends AjaxButton {

		public AddEnvironmentButton() {
			super("addEnvironment");
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			try {
				Environment environment = facade.createEnvironment(environmentName);
				setResponsePage(BuildPage.class, BuildPage.createPageParameters(environment.getName()));
			} catch (AlreadyExistsException e) {
				info(e);
			}
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
			super.onError(target);
		}
	}

	private class KillAllBuildsButton extends AjaxButton {

		public KillAllBuildsButton() {
			super("KillAllBuildsButton");
			setDefaultFormProcessing(false);
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			facade.killAll();
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
			super.onError(target);
		}
	}
}
