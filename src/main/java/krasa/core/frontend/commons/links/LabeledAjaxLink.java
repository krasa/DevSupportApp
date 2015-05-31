package krasa.core.frontend.commons.links;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

/**
 * @author Vojtech Krasa
 */
public abstract class LabeledAjaxLink<T> extends AjaxLink<T> {

	public LabeledAjaxLink(String id, IModel<T> model, IModel<String> labelModel) {
		super(id, model);
		setBody(labelModel);
	}

}
