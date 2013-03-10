package krasa.build.frontend.pages;

import java.util.List;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.components.EnvironmentsListPanel;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildPage extends BasePage {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static final String RESULT = "result";
	protected EnvironmentsListPanel environmets;
	@SpringBean
	private BuildFacade facade;
	private String environmentName;

	public BuildPage() {
		add(addEnvironmentForm());
		add(environmets = new EnvironmentsListPanel("environmets", getEnvironmentsModel()));
	}

	private Form addEnvironmentForm() {
		Form form = new Form("addEnvironmentForm");
		TextField<String> name = new TextField<String>("name", new PropertyModel<String>(this, "environmentName"));
		name.setRequired(true);
		form.add(name);
		form.add(new AjaxButton("addEnvironment") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				facade.createEnvironment(environmentName);
				target.add(environmets);
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
				super.onError(target, form);
			}
		});
		return form;
	}

	private IModel<List<Environment>> getEnvironmentsModel() {
		return new LoadableDetachableModel<List<Environment>>() {
			@Override
			protected List<Environment> load() {
				return facade.getEnvironments();
			}
		};
	}

}
