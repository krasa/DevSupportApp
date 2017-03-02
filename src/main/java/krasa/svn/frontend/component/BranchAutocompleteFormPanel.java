package krasa.svn.frontend.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.core.frontend.Ajax;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.components.BasePanel;
import krasa.svn.backend.facade.SvnFacade;
import krasa.svn.frontend.pages.config.ConfigurationPage;

public abstract class BranchAutocompleteFormPanel extends BasePanel {

	private static final Logger log = LoggerFactory.getLogger(BranchAutocompleteFormPanel.class);

	@SpringBean
	protected SvnFacade facade;
	protected BranchAutoCompletePanel autocomplete;
	protected FeedbackPanel feedback;

	public BranchAutocompleteFormPanel(String id) {
		this(id, null);
	}

	public BranchAutocompleteFormPanel(String addBranch, ResourceModel labelModel) {
		super(addBranch);
		add(createAddBranchForm(labelModel));
	}

	private Label createLabel(ResourceModel labelModel) {
		if (labelModel == null) {
			labelModel = new ResourceModel("branchName");
		}
		return new Label("label", labelModel);
	}

	protected Form createAddBranchForm(ResourceModel labelModel) {
		Form form = new Form("addBranchForm") {

			@Override
			protected void onError() {
				AjaxRequestTarget target = Ajax.getAjaxRequestTarget();
				if (target != null && feedback != null) {
					target.add(feedback);
				} else {
					log.warn("target is null, autocomplete={}", autocomplete.getFieldValue());
				}
			}
		};
		form.add(new AjaxFormSubmitBehavior(form, "submit") {
		});
		form.add(createLabel(labelModel));
		form.add(autocomplete = createAutoCompletePanel("autocomplete"));
		form.add(feedback = new MyFeedbackPanel("feedback"));
		form.add(new AddButton());
		form.add(new AddAllButton());
		form.add(new DeleteAllButton());
		form.add(new ConfigurationPage.RefreshBranchesButton(form, "refreshBranchesButton"));
		form.add(createBuildAllButton(form));
		form.add(createCheckBuildAllButton(form));
		return form;
	}

	protected Button createCheckBuildAllButton(Form form) {
		CheckBuildAllButton components = new CheckBuildAllButton(form, "CheckBuildAllButton") {

			@Override
			protected void buildAll(AjaxRequestTarget ajaxRequestTarget) {
				BranchAutocompleteFormPanel.this.checkBuildAll(ajaxRequestTarget);
			}
		};
		components.setVisible(false);
		return components;
	}

	protected Button createBuildAllButton(final Form form) {
		BuildAllButton components = new BuildAllButton(form, "buildAllButton") {

			@Override
			protected void buildAll(AjaxRequestTarget ajaxRequestTarget) {
				BranchAutocompleteFormPanel.this.buildAll(ajaxRequestTarget);
			}
		};
		components.setVisible(false);
		return components;
	}

	protected void checkBuildAll(AjaxRequestTarget ajaxRequestTarget) {

	}

	protected BranchAutoCompletePanel createAutoCompletePanel(String id) {
		return new BranchAutoCompletePanel(id);
	}

	protected abstract void deleteAllBranches(AjaxRequestTarget target);

	protected void buildAll(AjaxRequestTarget target) {

	}

	protected abstract void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target);

	protected abstract void addBranch(String fieldValue, AjaxRequestTarget target);

	protected void onUpdate(AjaxRequestTarget target) {
	}

	public AutoCompleteTextField<String> getField() {
		return autocomplete.getField();
	}

	private class DeleteAllButton extends AjaxButton {

		public DeleteAllButton() {
			super("deleteAll");
			setDefaultFormProcessing(false);
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			deleteAllBranches(target);
			onUpdate(target);
			autocomplete.resetFieldValue(target);
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
			super.onError(target);
			target.add(feedback);
		}
	}

	private class AddAllButton extends AjaxButton {

		public AddAllButton() {
			super("addAll");
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			String fieldValue = autocomplete.getFieldValue();
			if (StringUtils.isNotBlank(fieldValue)) {
				addAllMatchingBranches(fieldValue, target);
				onUpdate(target);
				autocomplete.resetFieldValue(target);
			}
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
			super.onError(target);
			target.add(feedback);
		}
	}

	private class AddButton extends AjaxButton {

		public AddButton() {
			super("add");
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			String fieldValue = autocomplete.getFieldValue();
			if (StringUtils.isNotBlank(fieldValue)) {
				addBranch(fieldValue, target);
				onUpdate(target);
				autocomplete.resetFieldValue(target);
			}
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
			super.onError(target);
			target.add(feedback);
		}
	}
}
