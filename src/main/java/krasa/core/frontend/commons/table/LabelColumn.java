package krasa.core.frontend.commons.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public abstract class LabelColumn<T> extends PropertyColumn<T, String> {

	public LabelColumn(IModel<String> displayModel) {
		super(displayModel, null);
	}

	@Override
	public IModel<Object> getDataModel(final IModel<T> rowModel) {
		return new AbstractReadOnlyModel<Object>() {
			@Override
			public Object getObject() {
				return getModel(rowModel);
			}
		};
	}

	protected abstract Object getModel(IModel<T> rowModel);

}
