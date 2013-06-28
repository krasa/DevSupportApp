package krasa.merge.frontend.component;

import krasa.core.frontend.Ajax;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.components.BasePanel;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class AddBranchFormPanel extends BasePanel {

	@SpringBean
	protected Facade facade;
	protected BranchAutoCompletePanel autocomplete;
	protected FeedbackPanel feedback;

	public AddBranchFormPanel(String id) {
		this(id, null);
	}

	public AddBranchFormPanel(String addBranch, ResourceModel labelModel) {
		super(addBranch);
		add(createAddBranchForm(labelModel));
	}

	private Label createLabel(ResourceModel labelModel) {
		if (labelModel == null) {
			labelModel = new ResourceModel("branchName");
		}
		return new Label("label", labelModel);
	}

	private Form createAddBranchForm(ResourceModel labelModel) {
		Form form = new Form("addBranchForm") {
			@Override
			protected void onSubmit() {
				AjaxRequestTarget target = Ajax.getAjaxRequestTarget();
				String fieldValue = autocomplete.getFieldValue();
				AddBranchFormPanel.this.addBranch(fieldValue);
				onUpdate(target);
				autocomplete.resetFieldValue(target);
			}

			@Override
			protected void onError() {
				AjaxRequestTarget target = Ajax.getAjaxRequestTarget();
				super.onError();
				target.add(feedback);
			}
		};
		form.add(new AjaxFormSubmitBehavior(form, "onsubmit") {
		});
		form.add(createLabel(labelModel));
		form.add(autocomplete = createAutoCompletePanel());
		form.add(feedback = new MyFeedbackPanel("feedback"));
		form.add(new AjaxButton("addAll") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String fieldValue = autocomplete.getFieldValue();
				addAllMatchingBranches(fieldValue);
				onUpdate(target);
				autocomplete.resetFieldValue(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(feedback);
			}
		});
		return form;
	}

	protected BranchAutoCompletePanel createAutoCompletePanel() {
		return new BranchAutoCompletePanel("autocomplete");
	}

	protected void addAllMatchingBranches(String fieldValue) {
		facade.addAllMatchingBranchesIntoProfile(fieldValue);
	}

	protected void addBranch(String fieldValue) {
		facade.addBranchIntoProfile(fieldValue);
	}

	protected abstract void onUpdate(AjaxRequestTarget target);

}
