package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.CheckBoxPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class CheckBoxColumn<BranchBuild> extends AbstractColumn<BranchBuild, String> {
	public CheckBoxColumn(IModel<String> displayModel, String sortProperty) {
		super(displayModel, sortProperty);
	}

	public CheckBoxColumn(IModel<String> displayModel) {
		super(displayModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<BranchBuild>> cellItem, String componentId, IModel<BranchBuild> model) {
		cellItem.add(new CheckBoxPanel<BranchBuild>(componentId, model) {
			@Override
			public boolean isChecked(IModel<BranchBuild> model) {
				return CheckBoxColumn.this.isChecked(model);
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel, IModel<BranchBuild> model) {
				CheckBoxColumn.this.onUpdate(target, booleanIModel, model);
				target.add(this);
			}
		});
	}

	protected abstract void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel, IModel<BranchBuild> model);

	protected abstract boolean isChecked(IModel<BranchBuild> model);
}
