package krasa.core.frontend.commons;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public abstract class DropDownChoicePanel extends Panel {

	public DropDownChoicePanel(String componentId, final PropertyModel<String> model,
			final IModel<List<String>> displayModel) {
		super(componentId, model);
		final DropDownChoice<String> models = new DropDownChoice<String>("drop", model, displayModel);
		models.setNullValid(true);
		models.setOutputMarkupId(true);
		models.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				DropDownChoicePanel.this.onUpdate(target, model);
			}
		});
		add(models);
	}

	protected abstract void onUpdate(AjaxRequestTarget target, PropertyModel<String> model);

}
