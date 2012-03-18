package krasa.frontend.pages.svn;

import krasa.backend.domain.SvnFolder;
import krasa.backend.dto.MergeInfoResult;
import krasa.backend.service.SvnLoaderProcessor;
import krasa.frontend.pages.BasePage;
import krasa.frontend.pages.commons.CheckBoxPanel;
import krasa.frontend.pages.components.SortableSvnFolderDataProvider;
import krasa.frontend.pages.mergeinfo.MergeInfoResultPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
public class SvnFolderBrowsePage extends BasePage {
    @SpringBean
    private SvnLoaderProcessor svnLoaderProcessor;

    public static final String PATH_PARAMETER = "path";
    public static final String MERGE_INFO = "mergeInfo";
    protected IModel<List<SvnFolder>> model;
    protected String path;
    protected final IModel<Set<String>> selectedBranches;
    protected AjaxFallbackDefaultDataTable<SvnFolder> table;

    public SvnFolderBrowsePage(PageParameters parameters) {
        super(parameters);
        StringValue name = parameters.get(PATH_PARAMETER);
        path = name.toString();
        initProjectList();
        initRefreshButton();
        EmptyPanel mergeInfo = new EmptyPanel(MERGE_INFO);
        mergeInfo.setOutputMarkupId(true);
        add(mergeInfo);

        selectedBranches = new SelectedBranchesModel(path);

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
                ajaxRequestTarget.add(table);
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
                MergeInfoResult mergeInfo = facade.getMergeInfo(path);
                MergeInfoResultPanel mergeInfo1 = new MergeInfoResultPanel(MERGE_INFO, mergeInfo);
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

    private void initProjectList() {
        SortableSvnFolderDataProvider sortableSvnFolderDataProvider = new SortableSvnFolderDataProvider(path);
        List<IColumn<SvnFolder>> columns = new ArrayList<IColumn<SvnFolder>>();
        columns.add(new AbstractColumn<SvnFolder>(new Model<String>("")) {
            public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
                                     IModel<SvnFolder> model) {
                cellItem.add(new CheckBoxPanel<SvnFolder>(componentId, model) {
                    @Override
                    public boolean isChecked(IModel<SvnFolder> model) {
                        return selectedBranches.getObject().contains(model.getObject().getName());
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel, IModel<SvnFolder> svnFolderIModel) {
                        facade.updateSelectionOfSvnFolder(svnFolderIModel.getObject(), booleanIModel.getObject());
                        target.add(this);
                    }
                });
            }
        });
        columns.add(new AbstractColumn<SvnFolder>(new Model<String>("kuk")) {
            public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
                                     IModel<SvnFolder> model) {
                cellItem.add(new TagItTextFieldPanel(componentId, path, Model.of("")));
            }
        });

        columns.add(new PropertyColumn<SvnFolder>(new Model<String>("name"), "name",
                "name"));
        table = new AjaxFallbackDefaultDataTable<SvnFolder>("table", columns,
                sortableSvnFolderDataProvider, 80);
        add(table);


    }

    private class SelectedBranchesModel extends LoadableDetachableModel<Set<String>> {
        String path;

        public SelectedBranchesModel(String path) {
            this.path = path;
        }

        @Override
        protected Set<String> load() {
            return facade.getSelectedBranchesName();
        }
    }
}
