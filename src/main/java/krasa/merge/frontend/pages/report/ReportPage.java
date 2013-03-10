package krasa.merge.frontend.pages.report;

import java.io.Serializable;

import krasa.core.frontend.MySession;
import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.frontend.components.BranchSelectionAddAutoCompletePanel;
import krasa.merge.frontend.components.SelectedBranchesTablePanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Vojtech Krasa
 */
public class ReportPage extends BasePage {
	public static final String RESULT = "result";
	protected final SelectedBranchesTablePanel branchesTable;
	protected MultiLineLabel resultLabel;

	public ReportPage() {
		add(new BranchSelectionAddAutoCompletePanel("ac", new ResourceModel("branchName")) {
			@Override
			public void process(AjaxRequestTarget target, String objectAsString) {
				facade.addSelectedBranch(objectAsString);
				target.add(branchesTable);
			}
		});
		resultLabel = new MultiLineLabel("resultLabel", new Model<Serializable>());
		resultLabel.setOutputMarkupPlaceholderTag(true);
		add(resultLabel);
		EmptyPanel emptyPanel = new EmptyPanel(RESULT);
		emptyPanel.setOutputMarkupPlaceholderTag(true);
		add(emptyPanel);
		// initSelectedBranchesList();
		Form form = new Form("form");
		form.add(new IndicatingAjaxButton("getReport") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ReportResult report = facade.getReport();
				ReportResultPanel result = new ReportResultPanel(RESULT, new Model<ReportResult>(report));
				ReportPage.this.replace(result);
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
				NotTaggedCommitsPanel result = new NotTaggedCommitsPanel(RESULT, new Model<ReportResult>(report));
				ReportPage.this.replace(result);
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
				resultLabel = new MultiLineLabel("result", new Model<Serializable>(result));
				resultLabel.setOutputMarkupPlaceholderTag(true);
				ReportPage.this.replace(resultLabel);
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
				resultLabel = new MultiLineLabel("result", new Model<Serializable>(result));
				resultLabel.setOutputMarkupPlaceholderTag(true);
				ReportPage.this.replace(resultLabel);
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
				ReportPage.this.replace(resultLabel);
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
		add(form);
		branchesTable = new SelectedBranchesTablePanel("branchesTable");
		add(branchesTable);

	}

}
