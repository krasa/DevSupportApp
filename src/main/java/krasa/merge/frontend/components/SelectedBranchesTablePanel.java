package krasa.merge.frontend.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.core.frontend.commons.CheckBoxPanel;
import krasa.core.frontend.commons.DropDownChoiceColumn;
import krasa.core.frontend.components.SortableSelectedBranchesDataProvider;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class SelectedBranchesTablePanel extends Panel {
	@SpringBean
	private Facade facade;

	protected AjaxFallbackDefaultDataTable<SvnFolder, String> table;

	public SelectedBranchesTablePanel(String id) {
		super(id);
		setOutputMarkupId(true);
		List<IColumn<SvnFolder, String>> columns = new ArrayList<IColumn<SvnFolder, String>>();
		columns.add(new AbstractColumn<SvnFolder, String>(new Model<String>("")) {
			public void populateItem(Item<ICellPopulator<SvnFolder>> cellItem, String componentId,
					IModel<SvnFolder> model) {
				cellItem.add(new CheckBoxPanel<SvnFolder>(componentId, model) {
					@Override
					public boolean isChecked(IModel<SvnFolder> model) {
						return true;
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
		columns.add(new PropertyColumn<SvnFolder, String>(new Model<String>("name"), "name", "name"));

		columns.add(new DropDownChoiceColumn<SvnFolder, String>(new Model<String>("searchFrom"), "searchFrom") {

			@Override
			protected IModel<List<String>> getDisplayModel(final IModel<SvnFolder> rowModel) {
				return new AbstractReadOnlyModel<List<String>>() {
					@Override
					public List<String> getObject() {
						String name = rowModel.getObject().getParent().getName();
						List<SvnFolder> allBranchesByProjectNme = facade.getAllBranchesByProjectNme(name);
						List<String> strings = new ArrayList<String>();
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
		});

		table = new AjaxFallbackDefaultDataTable<SvnFolder, String>("table", columns,
				new SortableSelectedBranchesDataProvider(), 80);
		add(table);

	}
}
