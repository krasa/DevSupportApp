package krasa.core.frontend.commons;

import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Vojtech Krasa
 */
public abstract class StandaloneAjaxCheckBox extends AjaxCheckBox {
	public StandaloneAjaxCheckBox(String id) {
		super(id);
		setDefaultModel(createModel());
	}

	private LoadableDetachableModel<Boolean> createModel() {
		return new LoadableDetachableModel<Boolean>() {
			@Override
			protected Boolean load() {
				return StandaloneAjaxCheckBox.this.load();
			}
		};
	}

	protected abstract Boolean load();

}
