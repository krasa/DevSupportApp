package krasa.core.frontend.commons.table;

import static org.apache.wicket.model.Model.of;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.model.IModel;

public class Columns {

	public static <T> PropertyColumn<T, String> column(String propertyExpression) {
		return new PropertyColumn<T, String>(of(propertyExpression), propertyExpression, propertyExpression);
	}

	public static <T> ChoiceFilteredPropertyColumn<T, String, String> column(String display, String propertyExpression,
			IModel<List<? extends String>> filterChoices) {
		return new ChoiceFilteredPropertyColumn<T, String, String>(of(display), propertyExpression, propertyExpression,
				filterChoices);
	}

	public static <T> ChoiceFilteredPropertyColumn<T, String, String> column(String propertyExpression,
			IModel<List<? extends String>> filterChoices) {
		return new ChoiceFilteredPropertyColumn<T, String, String>(of(propertyExpression), propertyExpression,
				propertyExpression,
				filterChoices);
	}

	public static <T> StyledChoiceFilteredPropertyColumn<T, String, String> styled(String display,
			String propertyExpression,
			IModel<List<? extends String>> filterChoices) {
		return new StyledChoiceFilteredPropertyColumn<T, String, String>(of(display), propertyExpression,
				propertyExpression, filterChoices);
	}

	public static <T> StyledChoiceFilteredPropertyColumn<T, String, String> styled(String propertyExpression,
			IModel<List<? extends String>> filterChoices) {
		return new StyledChoiceFilteredPropertyColumn<T, String, String>(of(propertyExpression), propertyExpression,
				propertyExpression, filterChoices);
	}
}
