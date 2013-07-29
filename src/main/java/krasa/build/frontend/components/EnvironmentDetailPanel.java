package krasa.build.frontend.components;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.Ajax;
import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.core.frontend.components.BasePanel;

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
	protected BuildComponentsTablePanel builds;

	public EnvironmentDetailPanel(String id, IModel<Environment> model) {
		super(id, model);
		this.environmentEntityModelWrapper = new EntityModelWrapper(model);
		add(createDeleteEnvironmentForm());
		add(createComponentsTableForm());
		add(createEnvironmentLabel());
		add(createAddComponentFormPanel());
	}

	private Label createEnvironmentLabel() {
		return new Label("environmentName", new PropertyModel<String>(environmentEntityModelWrapper, "name"));
	}

	private Form createDeleteEnvironmentForm() {
		Form form = new Form("deleteEnvironmentForm");
		form.add(createDeleteEnvironmentButton());
		return form;
	}

	private Form createComponentsTableForm() {
		Form form = new Form("componentsTableForm");
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

	private BuildComponentsTablePanel createComponentsTablePanel() {
		return new BuildComponentsTablePanel("builds", environmentEntityModelWrapper);
	}

	private AddComponentFormPanel createAddComponentFormPanel() {
		final AddComponentFormPanel autocomplete = new AddComponentFormPanel("addComponent", new ResourceModel(
				"componentName")) {

			@Override
			protected void addBranch(String fieldValue, AjaxRequestTarget target) {
				try {
					BuildableComponent buildableComponent = buildFacade.createBuildableComponent(
							environmentEntityModelWrapper.getObject(), fieldValue);
					builds.addItem(Ajax.getAjaxRequestTarget(), new BuildableComponentDto(buildableComponent));
				} catch (AlreadyExistsException e) {
					error(e.toString());
					Ajax.getAjaxRequestTarget().add(feedback);
				}
			}

			@Override
			protected void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target) {
				buildFacade.createBuildableComponentForAllMatchingComponents(environmentEntityModelWrapper.getObject(),
						fieldValue);
				target.add(builds);
			}
		};
		return autocomplete;
	}//
}
