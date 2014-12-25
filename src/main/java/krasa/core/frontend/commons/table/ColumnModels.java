package krasa.core.frontend.commons.table;

import java.util.List;

import org.apache.wicket.model.IModel;

public class ColumnModels {

	public static <T> ChoiceFilteredPropertyColumnModel<String, T> choice(IModel<List<T>> listIModel, String property) {
		return new ChoiceFilteredPropertyColumnModel<>(listIModel, property);
	}
}
