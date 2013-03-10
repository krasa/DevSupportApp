package krasa.merge.frontend.pages.svn;

import java.util.List;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.service.SvnLoaderProcessor;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoResultPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

/**
 * @author Vojtech Krasa
 */
public class SvnFolderBrowsePage extends BasePage {
	@SpringBean
	private SvnLoaderProcessor svnLoaderProcessor;

	public static final String PATH_PARAMETER = "path";
	public static final String MERGE_INFO = "mergeInfo";

	private IModel<List<SvnFolder>> model;
	private String path;
	private BranchesTablePanel branchesTablePanel;

	public SvnFolderBrowsePage(PageParameters parameters) {
		super(parameters);
		StringValue name = parameters.get(PATH_PARAMETER);
		path = name.toString();
		initRefreshButton();
		EmptyPanel mergeInfo = new EmptyPanel(MERGE_INFO);
		mergeInfo.setOutputMarkupId(true);
		add(mergeInfo);
		branchesTablePanel = new BranchesTablePanel("branchesTablePanel", path);
		branchesTablePanel.setOutputMarkupId(true);

		add(branchesTablePanel);

	}

	private void initRefreshButton() {
		final Form form = new Form("form");
		form.add(new Label("parent", path));

		form.setOutputMarkupId(true);
		form.add(new IndicatingAjaxButton("refreshProjectBraches") {
			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				svnLoaderProcessor.refreshBranchesByProjectName(path);
				ajaxRequestTarget.add(form);
				ajaxRequestTarget.add(branchesTablePanel);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});
		form.add(new AjaxCheckBox("mergeOnSubfolders", new LoadableDetachableModel<Boolean>() {
			@Override
			protected Boolean load() {
				return facade.isMergeOnSubFoldersForProject(path);
			}
		}) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				facade.setMergeOnSubFoldersForProject(path, getModelObject());
				target.add(this);
			}
		});
		add(new IndicatingAjaxButton("findMerges", form) {
			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				MergeInfoResultPanel mergeInfo1 = new MergeInfoResultPanel(MERGE_INFO,
						new LoadableDetachableModel<MergeInfoResult>() {
							@Override
							protected MergeInfoResult load() {
								return facade.getMergeInfo(path);
							}
						});
				ajaxRequestTarget.add(mergeInfo1);
				SvnFolderBrowsePage.this.replace(mergeInfo1);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});

		add(form);
	}

}
