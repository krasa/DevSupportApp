package krasa.svn.frontend.pages.mergeinfo;

import krasa.core.frontend.pages.BasePage;
import krasa.svn.backend.dto.MergeInfoResult;
import krasa.svn.frontend.component.BranchAutocompleteFormPanel;
import krasa.svn.frontend.component.table.SelectedBranchesTablePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.model.*;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoPage extends BasePage {

	private static final String RESULT = "result";
	protected SelectedBranchesTablePanel branchesTable;
	private Panel result;

	public MergeInfoPage() {
		queue(new MergeLeftPanel(LEFT));
		queue(createAddBranchIntoProfileFormPanel());
		queue(createResultPanel());
		queue(createBranchesTable());
		queue(createFindMergesForm());
	}

	private BranchAutocompleteFormPanel createAddBranchIntoProfileFormPanel() {
		return new BranchIntoProfileAutocompleteFormPanel();
	}

	private void update(AjaxRequestTarget target) {
		target.add(branchesTable);
	}

	private SelectedBranchesTablePanel createBranchesTable() {
		return branchesTable = new SelectedBranchesTablePanel("branchesTable");
	}

	private Panel createResultPanel() {
		result = new EmptyPanel(RESULT);
		result.setOutputMarkupPlaceholderTag(true);
		return result;
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

				MergeInfoPage.this.result.replaceWith(result);
				MergeInfoPage.this.result = result;
				target.add(result);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		return form;
	}

	private class BranchIntoProfileAutocompleteFormPanel extends BranchAutocompleteFormPanel {

		public BranchIntoProfileAutocompleteFormPanel() {
			super("addBranchPanel");
		}

		@Override
		protected Form createAddBranchForm(ResourceModel labelModel) {
			Form addBranchForm = super.createAddBranchForm(labelModel);
			addBranchForm.add(new ReplaceSearchFromButton("replaceSearchFrom"));
			addBranchForm.add(new ReplaceSearchFromToTrunkButton("replaceSearchFromToTrunk"));
			return addBranchForm;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			update(target);
		}

		private class ReplaceSearchFromButton extends AjaxButton {

			public ReplaceSearchFromButton(String replaceSearchFrom) {
				super(replaceSearchFrom);
				setDefaultFormProcessing(false);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				facade.replaceSearchFrom();
				update(target);
			}

		}

		private class ReplaceSearchFromToTrunkButton extends AjaxButton {

			public ReplaceSearchFromToTrunkButton(String replaceSearchFrom) {
				super(replaceSearchFrom);
				setDefaultFormProcessing(false);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				facade.replaceSearchFromToTrunk();
				update(target);
			}

		}

		@Override
		protected void deleteAllBranches(AjaxRequestTarget target) {
			facade.deleteAllBranchesFromProfile();
		}

		@Override
		protected void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target) {
			facade.addAllMatchingBranchesIntoProfile(fieldValue);
		}

		@Override
		protected void addBranch(String fieldValue, AjaxRequestTarget target) {
			facade.addBranchIntoProfile(fieldValue);
		}

	}

}
