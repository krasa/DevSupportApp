package krasa.core.frontend.commons.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class PanelColumn<T> extends AbstractColumn<T, String> {

	public PanelColumn(IModel<String> displayModel) {
		super(displayModel);
	}

	protected PanelColumn(IModel<String> displayModel, String sortProperty) {
		super(displayModel, sortProperty);
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
		cellItem.add(getPanel(componentId, rowModel));
	}

	protected abstract Panel getPanel(String componentId, IModel<T> rowModel);

}
