package krasa.core.frontend.commons;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class StyledLabel extends Label {
	public StyledLabel(String id, IModel<?> model) {
		super(id, model);
		add(new AttributeModifier("id", model));
	}
}
