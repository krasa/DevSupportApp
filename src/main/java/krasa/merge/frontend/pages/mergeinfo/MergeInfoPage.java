package krasa.merge.frontend.pages.mergeinfo;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.frontend.component.AddBranchFormPanel;
import krasa.merge.frontend.component.table.SelectedBranchesTablePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoPage extends BasePage {
	private static final String RESULT = "result";
	protected SelectedBranchesTablePanel branchesTable;

	public MergeInfoPage() {
		add(new MergeLeftPanel(LEFT));
		add(createAddBranchIntoProfileFormPanel());
		add(createResultPanel());
		add(createBranchesTable());
		add(createFindMergesForm());
	}

	private AddBranchFormPanel createAddBranchIntoProfileFormPanel() {
		return new AddBranchFormPanel("addBranchPanel") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(branchesTable);
			}
		};
	}

	private SelectedBranchesTablePanel createBranchesTable() {
		return branchesTable = new SelectedBranchesTablePanel("branchesTable");
	}

	private EmptyPanel createResultPanel() {
		EmptyPanel label = new EmptyPanel(RESULT);
		label.setOutputMarkupPlaceholderTag(true);
		return label;
	}

	private Form createFindMergesForm() {
		Form form = new Form("findMergesForm");
		form.add(new IndicatingAjaxButton("findMerges") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				MergeInfoResultPanel result = new MergeInfoResultPanel(RESULT,
						new LoadableDetachableModel<MergeInfoResult>() {
							@Override
							protected MergeInfoResult load() {
								return facade.getMergeInfoForAllSelectedBranches();
							}
						});

				MergeInfoPage.this.replace(result);
				target.add(result);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		return form;
	}

}
