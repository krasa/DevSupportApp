package krasa.build.frontend.pages.components;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.core.frontend.components.BasePanel;
import krasa.merge.frontend.component.AddBranchFormPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EnvironmentDetailPanel extends BasePanel {

	@SpringBean
	private BuildFacade buildFacade;
	private EntityModelWrapper<Environment> environmentEntityModelWrapper;
	protected ComponentsTablePanel builds;

	public EnvironmentDetailPanel(String id, IModel<Environment> model) {
		super(id, model);
		this.environmentEntityModelWrapper = new EntityModelWrapper(model);
		add(createForm());
		add(createEnvironmentLabel());
		add(createAddBranchFormPanel());
	}

	private Label createEnvironmentLabel() {
		return new Label("environmentName", new PropertyModel<String>(environmentEntityModelWrapper, "name"));
	}

	private Form createForm() {
		Form form = new Form("form");
		form.add(createDeleteEnvironmentButton());
		form.add(builds = createComponentsTablePanel());
		return form;
	}

	private AjaxButton createDeleteEnvironmentButton() {
		return new AjaxButton("delete") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				EnvironmentDetailPanel panel = EnvironmentDetailPanel.this;
				buildFacade.deleteEnvironment(panel.environmentEntityModelWrapper.getId());
				panel.setVisible(false);
				target.add(panel);
				super.onSubmit(target, form);
			}
		};
	}

	private ComponentsTablePanel createComponentsTablePanel() {
		return new ComponentsTablePanel("builds", environmentEntityModelWrapper);
	}

	private AddBranchFormPanel createAddBranchFormPanel() {
		final AddBranchFormPanel autocomplete = new AddBranchFormPanel("addBranchPanel", new ResourceModel(
				"componentName")) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(builds);
			}

			@Override
			protected void addBranch(String fieldValue) {
				buildFacade.addComponnet(environmentEntityModelWrapper.getObject(), fieldValue);
			}

			@Override
			protected void addAllMatchingBranches(String fieldValue) {
				buildFacade.addAllMatchingComponents(environmentEntityModelWrapper.getObject(), fieldValue);
			}
		};
		return autocomplete;
	}

}
