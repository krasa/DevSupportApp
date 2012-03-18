package krasa.frontend.pages.report;

import krasa.backend.async.Callback;
import krasa.backend.domain.ReportResult;
import krasa.backend.domain.SvnFolder;
import krasa.frontend.pages.BasePage;
import krasa.frontend.pages.commons.CheckBoxPanel;
import krasa.frontend.pages.components.BranchSelectionAddAutocompletePanel;
import krasa.frontend.pages.components.SortableSelectedBranchesDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class ReportPage extends BasePage {
    public static final String RESULT = "result";
    protected AjaxFallbackDefaultDataTable<SvnFolder> table;

    public ReportPage() {
        add(new BranchSelectionAddAutocompletePanel("ac", new Callback<AjaxRequestTarget>() {
            public void process(AjaxRequestTarget entry) {
                entry.add(table);
            }
        }));
        EmptyPanel label = new EmptyPanel(RESULT);
        label.setOutputMarkupPlaceholderTag(true);
        add(label);
        initSelectedBranchesList();
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
        add(form);

    }

    private void initSelectedBranchesList() {
        List<IColumn<SvnFolder>> columns = new ArrayList<IColumn<SvnFolder>>();
        columns.add(new AbstractColumn<SvnFolder>(new Model<String>("")) {
            public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
                                     IModel<SvnFolder> model) {
                cellItem.add(new CheckBoxPanel<SvnFolder>(componentId, model) {
                    @Override
                    public boolean isChecked(IModel<SvnFolder> model) {
                        return true;
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel, IModel<SvnFolder> svnFolderIModel) {
                        facade.updateSelectionOfSvnFolder(svnFolderIModel.getObject(), booleanIModel.getObject());
                        target.add(this);
                    }
                });
            }
        });

        columns.add(new PropertyColumn<SvnFolder>(new Model<String>("name"), "name", "name"));
        table = new AjaxFallbackDefaultDataTable<SvnFolder>("table", columns,
                new SortableSelectedBranchesDataProvider(), 80);
        add(table);

    }

}
