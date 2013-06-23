package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.EditablePanel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class PropertyEditableColumn<T, S> extends AbstractColumn<T, S> {

	private String propertyExpression;

	private int size = 100;

	public PropertyEditableColumn(IModel<String> displayModel, String propertyExpression, int size) {
		super(displayModel);
		this.size = size;
		this.propertyExpression = propertyExpression;
	}

	public PropertyEditableColumn(IModel<String> displayModel, S sortProperty, String propertyExpression, int size) {
		super(displayModel, sortProperty);
		this.size = size;
		this.propertyExpression = propertyExpression;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected void decoratePanel(EditablePanel<T> panel) {
		panel.getTextfield().add(new AttributeModifier("style", "width:" + size + "px"));
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		EditablePanel<T> panel = new EditablePanel<T>(componentId, rowModel, new PropertyModel<S>(rowModel,
				propertyExpression));
		decoratePanel(panel);
		item.add(panel);
	}
}
