package krasa.core.frontend.commons.table;

import java.util.Date;

import krasa.core.frontend.commons.DateModel;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class DateColumn<T> extends PropertyColumn<T, String> {

	private String datePattern = DateModel.DD_MM_YYYY_HH_MM;

	public DateColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String datePattern) {
		super(displayModel, sortProperty, propertyExpression);
		this.datePattern = datePattern;
	}

	public DateColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}

	public DateColumn(IModel<String> displayModel, String propertyExpression) {
		super(displayModel, propertyExpression);
	}

	@Override
	public IModel<Object> getDataModel(IModel<T> rowModel) {
		PropertyModel<Date> propertyModel = new PropertyModel<>(rowModel, getPropertyExpression());
		return new DateModel(propertyModel, datePattern);
	}
}
