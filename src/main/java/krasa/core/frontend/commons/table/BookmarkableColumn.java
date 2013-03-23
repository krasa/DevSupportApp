package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.ProjectLinkPanel;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class BookmarkableColumn<T, U> extends AbstractColumn<T, U> {

	private String name;
	private String pathExpression;

	public BookmarkableColumn(IModel<String> displayModel, U propertyExpression, String pathExpression) {
		super(displayModel, propertyExpression);
		name = "name";
		this.pathExpression = pathExpression;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
		cellItem.add(getComponents(componentId, rowModel));
	}

	protected ProjectLinkPanel getComponents(String componentId, IModel<T> rowModel) {
		return new ProjectLinkPanel(componentId, new PropertyModel<String>(rowModel, name), new PropertyModel<String>(
				rowModel, pathExpression));
	}
}
