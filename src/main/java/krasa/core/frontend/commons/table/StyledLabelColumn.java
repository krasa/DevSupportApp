package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

public class StyledLabelColumn<T> extends PanelColumn<T> {

	private String property;

	public StyledLabelColumn(Model<String> displayModel, String property) {
		super(displayModel, property);
		this.property = property;
	}

	@Override
	protected Panel getPanel(String componentId, IModel<T> rowModel) {
		return new LabelPanel(componentId, new PropertyModel<>(rowModel, property)) {

			@Override
			protected Component getComponent(String id, IModel labelModel) {
				return new StyledLabel(id, labelModel);
			}
		};
	}
}
