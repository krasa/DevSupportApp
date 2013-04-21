package krasa.build.frontend.pages;

import java.util.Collections;
import java.util.List;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.components.BuildLeftPanel;
import krasa.build.frontend.pages.components.EnvironmentsListPanel;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildPage extends BasePage {
	public static final String NAME = "env";
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static final String RESULT = "result";
	protected EnvironmentsListPanel environmets;
	@SpringBean
	private BuildFacade facade;
	private String environmentName;
	protected IModel<Environment> model;

	public BuildPage() {
		add(addEnvironmentForm());
		add(environmets = new EnvironmentsListPanel("environmets", getEnvironmentsModel()));
	}

	public BuildPage(PageParameters parameters) {
		super(parameters);
		final StringValue stringValue = parameters.get(NAME);
		model = getEnvironmentModel(stringValue);
		add(addEnvironmentForm());
		add(environmets = new EnvironmentsListPanel("environmets", getEnvironmentsModel(model)));
	}

	private Form addEnvironmentForm() {
		Form form = new Form("addEnvironmentForm");
		TextField<String> name = new TextField<String>("name", new PropertyModel<String>(this, "environmentName"));
		name.setRequired(true);
		form.add(name);
		form.add(new AjaxButton("addEnvironment") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					Environment environment = facade.createEnvironment(environmentName);
					setResponsePage(BuildPage.class, BuildPage.createPageParameters(environment));
				} catch (AlreadyExistsException e) {
					info(e);
					target.add(getFeedbackPanel());
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
				super.onError(target, form);
			}
		});
		return form;
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new BuildLeftPanel(id, model);
	}

	private IModel<List<Environment>> getEnvironmentsModel() {
		return new LoadableDetachableModel<List<Environment>>() {
			@Override
			protected List<Environment> load() {
				return facade.getEnvironments();
			}
		};
	}

	private AbstractReadOnlyModel<List<Environment>> getEnvironmentsModel(final IModel<Environment> model1) {
		return new AbstractReadOnlyModel<List<Environment>>() {
			@Override
			public List<Environment> getObject() {
				return Collections.singletonList(model1.getObject());
			}
		};
	}

	private LoadableDetachableModel<Environment> getEnvironmentModel(final StringValue stringValue) {
		return new LoadableDetachableModel<Environment>() {
			@Override
			protected Environment load() {
				return facade.getEnvironmentByName(stringValue.toString());
			}
		};
	}

	public static PageParameters createPageParameters(Environment modelObject) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(NAME, modelObject.getName());
		return pageParameters;
	}
}
