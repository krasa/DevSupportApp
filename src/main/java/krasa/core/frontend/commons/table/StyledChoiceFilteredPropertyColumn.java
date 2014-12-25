package krasa.core.frontend.commons.table;

import java.util.List;

import krasa.core.frontend.commons.StyledLabel;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public class StyledChoiceFilteredPropertyColumn<T, Y, S> extends ChoiceFilteredPropertyColumn<T, Y, S> {

	public StyledChoiceFilteredPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
			IModel<List<? extends Y>> filterChoices) {
		super(displayModel, sortProperty, propertyExpression, filterChoices);
	}

	public StyledChoiceFilteredPropertyColumn(IModel<String> displayModel, String propertyExpression,
			IModel<List<? extends Y>> filterChoices) {
		super(displayModel, propertyExpression, filterChoices);
	}

	@Override
	public void populateItem(final Item<ICellPopulator<T>> item, final String componentId,
			final IModel<T> rowModel)
	{
		item.add(new StyledLabel(componentId, getDataModel(rowModel)));
	}

}
