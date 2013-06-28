package krasa.build.frontend.components;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.components.BasePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CreateEnvironmentFormPanel extends BasePanel {
	@SpringBean
	private BuildFacade facade;

	private String environmentName;

	public CreateEnvironmentFormPanel(String id) {
		super(id);
		add(createForm());
	}

	private Form createForm() {
		Form form = new Form("createEnvironmentForm");
		TextField<String> name = new TextField<>("name", new PropertyModel<String>(this, "environmentName"));
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
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
			}
		});
		return form;
	}
}
