package krasa.build.frontend.pages.components;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.merge.frontend.components.BranchSelectionAddAutoCompletePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EnvironmentDetailPanel extends Panel {

	@SpringBean
	private BuildFacade facade;
	private EntityModelWrapper<Environment> modelWrapper;
	protected final ComponentsTablePanel builds;

	public EnvironmentDetailPanel(String id, IModel<Environment> model) {
		super(id, model);
		setOutputMarkupId(true);
		this.modelWrapper = new EntityModelWrapper(model);
		Form form = new Form("form");
		add(form);
		form.add(new AjaxButton("delete") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				EnvironmentDetailPanel panel = EnvironmentDetailPanel.this;
				facade.deleteEnvironment(panel.modelWrapper.getId());
				panel.setVisible(false);
				target.add(panel);
				super.onSubmit(target, form);
			}
		});
		builds = new ComponentsTablePanel("builds", modelWrapper);
		form.add(builds);

		add(getBranchAutocomplete(modelWrapper, builds));
		add(new Label("environmentName", new PropertyModel<String>(modelWrapper, "name")));
	}

	private BranchSelectionAddAutoCompletePanel getBranchAutocomplete(final IModel<Environment> environment,
			final ComponentsTablePanel builds) {
		return new BranchSelectionAddAutoCompletePanel("addComponent", new ResourceModel("componentName")) {

			@Override
			public void process(AjaxRequestTarget target, String componentName) {
				facade.addComponnet(environment.getObject(), componentName);
				target.add(builds);
			}
		};
	}

}
