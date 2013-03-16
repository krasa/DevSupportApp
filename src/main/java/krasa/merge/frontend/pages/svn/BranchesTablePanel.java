package krasa.merge.frontend.pages.svn;

import java.util.ArrayList;
import java.util.List;

import krasa.core.frontend.commons.CheckBoxPanel;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.facade.Facade;
import krasa.merge.frontend.component.table.SortableSvnFolderDataProvider;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class BranchesTablePanel extends Panel {

	@SpringBean
	private Facade facade;

	protected IModel<List<String>> selectedBranches;
	protected AjaxFallbackDefaultDataTable<SvnFolder, String> table;

	public BranchesTablePanel(String id, final String path) {
		super(id);
		selectedBranches = new SelectedBranchesModel(path);

		SortableSvnFolderDataProvider sortableSvnFolderDataProvider = new SortableSvnFolderDataProvider(path);
		table = new AjaxFallbackDefaultDataTable<SvnFolder, String>("table", getColumns(),
				sortableSvnFolderDataProvider, 80);
		add(table);

		setOutputMarkupId(true);

	}

	private List<IColumn<SvnFolder, String>> getColumns() {
		List<IColumn<SvnFolder, String>> columns = new ArrayList<IColumn<SvnFolder, String>>();
		columns.add(new AbstractColumn<SvnFolder, String>(new Model<String>("")) {
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
		});
		// columns.add(new AbstractColumn<SvnFolder>(new Model<String>("kuk")) {
		// public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
		// IModel<SvnFolder> model) {
		// cellItem.add(new TagItTextFieldPanel(componentId, path, Model.of("")));
		// }
		// });

		columns.add(new PropertyColumn<SvnFolder, String>(new Model<String>("name"), "name", "name"));
		return columns;
	}

	private class SelectedBranchesModel extends LoadableDetachableModel<List<String>> {
		String path;

		public SelectedBranchesModel(String path) {
			this.path = path;
		}

		@Override
		protected List<String> load() {
			return facade.getSelectedBranchesNames();
		}
	}
}
