package krasa.svn.frontend.component.table;

import java.util.*;

import krasa.core.frontend.commons.CheckBoxPanel;
import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.components.BasePanel;
import krasa.svn.backend.domain.SvnFolder;
import krasa.svn.backend.facade.SvnFacade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class BranchesTablePanel extends BasePanel {

	@SpringBean
	private SvnFacade facade;
	protected IModel<List<String>> selectedBranches;
	protected AjaxFallbackDefaultDataTable<SvnFolder, String> table;
	protected final Form form;

	public BranchesTablePanel(String id) {
		super(id);
		add(form = createForm());
		form.add(table = createTable(new SortableSelectedBranchesDataProvider()));
	}

	public BranchesTablePanel(String id, String path) {
		super(id);
		selectedBranches = new SelectedBranchesModel(facade);
		add(form = createForm());
		form.add(table = createTable(new SortableSvnFolderDataProvider(path)));
	}

	private Form createForm() {
		return new Form("form");
	}

	private AjaxFallbackDefaultDataTable<SvnFolder, String> createTable(ISortableDataProvider dataProvider) {
		List<IColumn<SvnFolder, String>> columns = createColumns();
		return new AjaxFallbackDefaultDataTable<>("table", columns, dataProvider, Integer.MAX_VALUE);
	}

	protected List<IColumn<SvnFolder, String>> createColumns() {
		List<IColumn<SvnFolder, String>> columns = new ArrayList<>();
		columns.add(createCheckBoxColumn());
		columns.add(createNameColumn());
		columns.add(createSearchFromColumn());
		return columns;
	}

	protected AbstractColumn<SvnFolder, String> createNameColumn() {
		return new ProjectLinkColumn(new Model<>("branch name"), "name", "path");
	}

	protected DropDownChoiceColumn<SvnFolder, String> createSearchFromColumn() {
		return new DropDownChoiceColumn<SvnFolder, String>(new Model<>("searchFrom"), "searchFrom") {

			@Override
			protected IModel<List<String>> getDisplayModel(final IModel<SvnFolder> rowModel) {
				return new AbstractReadOnlyModel<List<String>>() {

					@Override
					public List<String> getObject() {
						String name = rowModel.getObject().getParent().getName();
						List<SvnFolder> allBranchesByProjectNme = facade.getAllBranchesByProjectName(name);
						List<String> strings = new ArrayList<>();
						for (SvnFolder folder : allBranchesByProjectNme) {
							strings.add(folder.getName());
						}
						Collections.sort(strings);
						Collections.reverse(strings);
						return strings;
					}
				};
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target, PropertyModel<String> model) {
				super.onUpdate(target, model);
				facade.updateBranch((SvnFolder) model.getInnermostModelOrObject());
			}
		};
	}

	private AbstractColumn<SvnFolder, String> createCheckBoxColumn() {
		return new AbstractColumn<SvnFolder, String>(new Model<>("")) {

			@Override
			public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
					IModel<SvnFolder> model) {
				cellItem.add(new CheckBoxPanel<SvnFolder>(componentId, model) {

					@Override
					public boolean isChecked(IModel<SvnFolder> model) {
						return selectedBranches.getObject().contains(model.getObject().getName());
					}

					@Override
					protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel,
							IModel<SvnFolder> svnFolderIModel) {
						facade.updateSelectionOfSvnFolder(svnFolderIModel.getObject(), booleanIModel.getObject());
						target.add(this);
					}
				});
			}
		};
	}
}
