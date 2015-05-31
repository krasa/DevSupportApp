package krasa.core.frontend.commons;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

public abstract class DropDownChoicePanel extends Panel {

	public DropDownChoicePanel(String componentId, final PropertyModel<String> model, IModel<List<String>> displayModel) {
		super(componentId, model);
		DropDownChoice<String> models = new DropDownChoice<>("drop", model, displayModel);
		models.setNullValid(true);
		models.setOutputMarkupId(true);
		models.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				DropDownChoicePanel.this.onUpdate(target, model);
			}
		});
		add(models);
	}

	protected abstract void onUpdate(AjaxRequestTarget target, PropertyModel<String> model);

}
