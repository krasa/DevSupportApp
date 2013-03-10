package krasa.core.frontend.commons;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class PropertyEditableColumn extends AbstractColumn {

	private String propertyExpression;

	private int size = 100;

	public PropertyEditableColumn(IModel displayModel, String propertyExpression, int size) {
		super(displayModel);
		this.size = size;
		this.propertyExpression = propertyExpression;
	}

	public PropertyEditableColumn(IModel displayModel, String sortProperty, String propertyExpression, int size) {
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

	public void populateItem(Item item, String componentId, IModel rowModel) {
		item.add(new EditablePanel(componentId, new PropertyModel(rowModel, propertyExpression)));
		item.add(new AttributeModifier("style", "width:" + size + "px"));
	}

}
