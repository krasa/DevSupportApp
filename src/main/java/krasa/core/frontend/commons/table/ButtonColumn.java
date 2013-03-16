package krasa.core.frontend.commons.table;

import krasa.core.frontend.commons.ButtonPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class ButtonColumn<T> extends AbstractColumn<T, String> {

	protected String delete;

	public ButtonColumn(IModel<String> displayModel, String sortProperty) {
		super(displayModel, sortProperty);
	}

	public ButtonColumn(IModel<String> displayModel) {
		super(displayModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> components, String s, final IModel<T> model) {
		components.add(new ButtonPanel(s, ButtonColumn.this.getDisplayModel().getObject()) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ButtonColumn.this.onSubmit(model, target, form);
			}
		});

	}

	abstract protected void onSubmit(IModel<T> model, AjaxRequestTarget target, Form<?> form);

}
