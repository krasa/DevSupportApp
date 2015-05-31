package krasa.merge.frontend.pages.report;

import java.io.Serializable;

import krasa.core.frontend.MySession;
import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.frontend.component.BranchAutocompleteFormPanel;
import krasa.merge.frontend.component.table.SelectedBranchesTablePanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;

/**
 * @author Vojtech Krasa
 */
public class ReportPage extends BasePage {

	public static final String RESULT = "result";
	protected SelectedBranchesTablePanel branchesTable;
	protected MultiLineLabel resultLabel;

	public ReportPage() {
		queue(createAddBranchIntoProfileFormPanel());
		queue(createResultLabel());
		queue(createResultPanel());
		queue(createForm());
		queue(createBranchesTable());

	}

	private SelectedBranchesTablePanel createBranchesTable() {
		return branchesTable = new SelectedBranchesTablePanel("branchesTable");
	}

	private Component createResultLabel() {
		resultLabel = new MultiLineLabel("resultLabel", new Model<>());
		return resultLabel.setOutputMarkupPlaceholderTag(true);
	}

	private EmptyPanel createResultPanel() {
		EmptyPanel emptyPanel = new EmptyPanel(RESULT);
		emptyPanel.setOutputMarkupPlaceholderTag(true);
		return emptyPanel;
	}

	private Form createForm() {
		Form form = new Form("form");
		form.add(new IndicatingAjaxButton("getReport") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ReportResult report = facade.getReport();
				ReportResultPanel result = new ReportResultPanel(RESULT, new Model<>(report));
				getParent().get(RESULT).replaceWith(result);
				target.add(result);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(new IndicatingAjaxButton("findNotTagged") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ReportResult report = facade.getReport();
				NotTaggedCommitsPanel result = new NotTaggedCommitsPanel(RESULT, new Model<>(report));
				getParent().get(RESULT).replaceWith(result);
				target.add(result);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(new IndicatingAjaxButton("runRNS") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Profile current = MySession.get().getCurrent();
				String result = facade.runRns(current.getName());
				resultLabel = new MultiLineLabel(RESULT, new Model<Serializable>(result));
				resultLabel.setOutputMarkupPlaceholderTag(true);
				getParent().get(RESULT).replaceWith(resultLabel);
				target.add(resultLabel);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				Profile current = MySession.get().getCurrent();
				setEnabled(current.getType() == Profile.Type.FROM_SVN);
			}
		});
		form.add(new IndicatingAjaxButton("runVersionsOnPrgens") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String result = facade.runVersionsOnPrgens();
				resultLabel = new MultiLineLabel(RESULT, new Model<Serializable>(result));
				resultLabel.setOutputMarkupPlaceholderTag(true);
				getParent().get(RESULT).replaceWith(resultLabel);
				target.add(resultLabel);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(new IndicatingAjaxButton("runSvnHeadVsLastTag") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Profile current = MySession.get().getCurrent();
				String result = facade.runSvnHeadVsLastTag(current.getName());
				resultLabel = new MultiLineLabel("result", new Model<Serializable>(result));
				resultLabel.setOutputMarkupPlaceholderTag(true);
				getParent().get(RESULT).replaceWith(resultLabel);
				target.add(resultLabel);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				Profile current = MySession.get().getCurrent();
				setEnabled(current.getType() == Profile.Type.FROM_SVN);
			}

		});
		// final Model<String> of = Model.of("");
		// TagItTextField<String> portal = new TagItTextField<String>("tagit", of) {
		//
		// @Override
		// protected Iterable<String> getChoices(String input) {
		// System.err.println(of.getObject());
		// System.err.println(this.getModelObject());
		// System.err.println(this.getModelValue());
		// return facade.getSuggestions("portal", input);
		// }
		// };
		// form.add(portal);
		// portal.add(new OnChangeAjaxBehavior() {
		// @Override
		// protected void onUpdate(AjaxRequestTarget target) {
		// System.err.println(of.getObject());
		//
		// }
		// });
		return form;
	}

	private BranchAutocompleteFormPanel createAddBranchIntoProfileFormPanel() {
		return new BranchAutocompleteFormPanel("addBranchPanel") {

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

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(branchesTable);
			}
		};
	}

}
