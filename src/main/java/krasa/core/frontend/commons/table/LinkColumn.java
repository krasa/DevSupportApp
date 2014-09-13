package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.LinkPanel;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class LinkColumn<T, U> extends AbstractColumn<T, U> {

	private IModel<String> displayModel;

	public LinkColumn(IModel<String> displayModel, U propertyExpression) {
		super(displayModel, propertyExpression);
		this.displayModel = displayModel;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
		cellItem.add(getComponents(componentId, rowModel));
	}

	protected LinkPanel getComponents(String componentId, IModel<T> rowModel) {
		return new LinkPanel<T>(componentId, displayModel, rowModel) {

			@Override
			protected AbstractLink getComponent(String id, IModel<String> labelModel, IModel<T> rowModel) {
				return LinkColumn.this.getLinkComponent(id, labelModel, rowModel);
			}
		};
	}

	protected abstract AbstractLink getLinkComponent(String id, IModel<String> labelModel, IModel<T> rowModel);

}
