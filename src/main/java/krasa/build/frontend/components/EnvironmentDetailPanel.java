package krasa.build.frontend.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.AlreadyExistsException;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.backend.facade.UsernameException;
import krasa.build.frontend.pages.BuildPage;
import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.core.frontend.components.BasePanel;
import krasa.core.frontend.pages.BasePage;
import krasa.svn.frontend.component.BranchAutoCompletePanel;
import krasa.svn.frontend.component.BranchAutocompleteFormPanel;

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

	private Component createEnvironmentLabel() {
		PageParameters pageParameters = BuildPage.createPageParameters(
				environmentEntityModelWrapper.getObject().getName());
		PropertyModel<String> labelModel = new PropertyModel<>(environmentEntityModelWrapper, "name");
		return new LabeledBookmarkablePageLink("environmentName", BuildPage.class, pageParameters, labelModel);
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
			protected void onSubmit(AjaxRequestTarget target) {
				EnvironmentDetailPanel panel = EnvironmentDetailPanel.this;
				buildFacade.deleteEnvironment(panel.environmentEntityModelWrapper.getId());
				panel.setVisible(false);
				target.add(panel);
				super.onSubmit(target);
			}
		};
	}

	private BuildComponentsTablePanel createComponentsTablePanel() {
		return new BuildComponentsTablePanel("builds", environmentEntityModelWrapper);
	}

	private BranchAutocompleteFormPanel createAddComponentFormPanel() {
		BranchAutocompleteFormPanel autocomplete = new BranchAutocompleteFormPanel("addComponent", new ResourceModel(
				"componentName")) {

			@Override
			protected Button createCheckBuildAllButton(Form form) {
				Button checkBuildAllButton = super.createCheckBuildAllButton(form);
				checkBuildAllButton.setVisible(true);
				return checkBuildAllButton;
			}

			@Override
			protected Button createBuildAllButton(Form form) {
				Button buildAllButton = super.createBuildAllButton(form);
				buildAllButton.setVisible(true);
				return buildAllButton;
			}

			@Override
			protected BranchAutoCompletePanel createAutoCompletePanel(String id) {
				return new BranchesAndTagsAutoCompletePanel(id);
			}

			@Override
			protected void addBranch(String fieldValue, AjaxRequestTarget target) {
				try {
					BuildableComponent buildableComponent = buildFacade.createBuildableComponent(
							environmentEntityModelWrapper.getObject(), fieldValue);
					if (buildableComponent != null) {
						builds.table.addItem(target, new BuildableComponentDto(buildableComponent));
					}
				} catch (AlreadyExistsException e) {
					error(e.toString());
					target.add(feedback);
				}
			}

			@Override
			protected void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target) {
				buildFacade.createBuildableComponentForAllMatchingComponents(environmentEntityModelWrapper.getObject(),
						fieldValue);
				target.add(builds);
			}

			@Override
			protected void deleteAllBranches(AjaxRequestTarget target) {
				buildFacade.deleteAllBuildableComponents(environmentEntityModelWrapper.getObject());
				target.add(builds);
			}

			@Override
			protected void buildAll(AjaxRequestTarget target) {
				try {
					buildFacade.buildAll(environmentEntityModelWrapper.getObject());
					target.add(builds);
				} catch (UsernameException e) {
					error(e.getMessage());
					target.add(((BasePage) this.getPage()).getFeedbackPanel());
					target.appendJavaScript("alert('" + e.getMessage() + "');");
				}
			}

			@Override
			protected void checkBuildAll(AjaxRequestTarget target) {
				buildFacade.checkBuildAll(environmentEntityModelWrapper.getObject());
				target.add(builds);
			}
		};
		return autocomplete;
	}//
}
