package krasa.merge.frontend.pages.mergeinfo;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.frontend.components.BranchSelectionAddAutoCompletePanel;
import krasa.merge.frontend.components.SelectedBranchesTablePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoPage extends BasePage {
	private static final String RESULT = "result";
	protected final SelectedBranchesTablePanel branchesTable;

	public MergeInfoPage() {
		add(new BranchSelectionAddAutoCompletePanel("ac", new ResourceModel("branchName")) {
			@Override
			public void process(AjaxRequestTarget target, String objectAsString) {
				facade.addSelectedBranch(objectAsString);
				target.add(branchesTable);
			}
		});
		EmptyPanel label = new EmptyPanel(RESULT);
		label.setOutputMarkupPlaceholderTag(true);
		add(label);
		branchesTable = new SelectedBranchesTablePanel("branchesTable");
		add(branchesTable);
		Form form = new Form("form");
		form.add(new IndicatingAjaxButton("findMerges") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				MergeInfoResultPanel result = new MergeInfoResultPanel(RESULT,
						new LoadableDetachableModel<MergeInfoResult>() {
							@Override
							protected MergeInfoResult load() {
								return facade.getMergeInfo();
							}
						});

				MergeInfoPage.this.replace(result);
				target.add(result);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});

		add(form);
	}

}
