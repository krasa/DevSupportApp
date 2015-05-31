package krasa.svn.frontend.component;

import krasa.core.frontend.Ajax;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.components.BasePanel;
import krasa.svn.backend.facade.SvnFacade;
import krasa.svn.frontend.pages.config.ConfigurationPage;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;

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
		form.add(new AjaxButton("add") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String fieldValue = autocomplete.getFieldValue();
				if (StringUtils.isNotBlank(fieldValue)) {
					addBranch(fieldValue, target);
					onUpdate(target);
					autocomplete.resetFieldValue(target);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(feedback);
			}
		});
		form.add(new AjaxButton("addAll") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String fieldValue = autocomplete.getFieldValue();
				if (StringUtils.isNotBlank(fieldValue)) {
					addAllMatchingBranches(fieldValue, target);
					onUpdate(target);
					autocomplete.resetFieldValue(target);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(feedback);
			}
		});
		AjaxButton deleteAll = new AjaxButton("deleteAll") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				deleteAllBranches(target);
				onUpdate(target);
				autocomplete.resetFieldValue(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(feedback);
			}
		};
		deleteAll.setDefaultFormProcessing(false);
		form.add(deleteAll);
		form.add(new ConfigurationPage.RefreshBranchesButton(form, "refreshBranchesButton"));
		return form;
	}

	protected BranchAutoCompletePanel createAutoCompletePanel(String id) {
		return new BranchAutoCompletePanel(id);
	}

	protected abstract void deleteAllBranches(AjaxRequestTarget target);

	protected abstract void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target);

	protected abstract void addBranch(String fieldValue, AjaxRequestTarget target);

	protected void onUpdate(AjaxRequestTarget target) {
	}

	public AutoCompleteTextField<String> getField() {
		return autocomplete.getField();
	}
}
