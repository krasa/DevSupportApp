package krasa.merge.frontend.pages.svn;

import krasa.core.frontend.commons.StandaloneAjaxCheckBox;
import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.service.SvnFolderRefreshService;
import krasa.merge.frontend.component.table.BranchesTablePanel;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoResultPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

/**
 * @author Vojtech Krasa
 */
public class SvnFolderBrowsePage extends BasePage {

	@SpringBean
	private SvnFolderRefreshService svnFolderResfreshService;

	public static final String PATH_PARAMETER = "path";
	public static final String MERGE_INFO = "mergeInfo";

	private String path;
	private BranchesTablePanel branchesTablePanel;

	public SvnFolderBrowsePage(PageParameters parameters) {
		super(parameters);
		StringValue name = parameters.get(PATH_PARAMETER);
		String path1 = name.toString();
		if (path1 == null || path1.isEmpty()) {
			throw new IllegalStateException(PATH_PARAMETER + " parameter is empty");
		}
		path = facade.resolveProjectByPath(path1);
		createForm();
		add(createResultPanel());
		add(branchesTablePanel = createTable());

	}

	private EmptyPanel createResultPanel() {
		EmptyPanel mergeInfo = new EmptyPanel(MERGE_INFO);
		mergeInfo.setOutputMarkupId(true);
		return mergeInfo;
	}

	private BranchesTablePanel createTable() {
		return new BranchesTablePanel("branchesTablePanel", path);
	}

	private void createForm() {
		final Form form = new Form("form");
		form.add(new Label("parent", path));

		form.setOutputMarkupId(true);
		form.add(new IndicatingAjaxButton("refreshProjectBraches") {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				svnFolderResfreshService.refreshProjectByName(path);
				ajaxRequestTarget.add(form);
				ajaxRequestTarget.add(branchesTablePanel);
				info("Processing");
			}

			@Override
			protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				error("Error");
			}
		});
		form.add(createMergeOnSubfoldersCheckbox());
		form.add(createLoadTagsCheckbox());
		add(new IndicatingAjaxButton("findMerges", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
				MergeInfoResultPanel mergeInfo1 = new MergeInfoResultPanel(MERGE_INFO,
						new LoadableDetachableModel<MergeInfoResult>() {

							@Override
							protected MergeInfoResult load() {
								return facade.getMergeInfoForAllSelectedBranchesInProject(path);
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

	private StandaloneAjaxCheckBox createLoadTagsCheckbox() {
		return new StandaloneAjaxCheckBox("loadTags") {

			@Override
			protected Boolean load() {
				return facade.isLoadTags(path);
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				facade.setLoadTagsForProject(path, getModelObject());
				target.add(this);
			}
		};

	}

	private AjaxCheckBox createMergeOnSubfoldersCheckbox() {
		return new StandaloneAjaxCheckBox("mergeOnSubfolders") {

			@Override
			protected Boolean load() {
				return facade.isMergeOnSubFoldersForProject(path);
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				facade.setMergeOnSubFoldersForProject(path, getModelObject());
				target.add(this);
			}
		};
	}

	public static PageParameters parameters(IModel<SvnFolder> rowModel) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add("project", rowModel.getObject().getName());
		return pageParameters;
	}
}
