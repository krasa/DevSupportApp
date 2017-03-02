package krasa.core.frontend.commons.table;

import krasa.core.frontend.StaticImage;
import krasa.core.frontend.commons.ButtonPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class ButtonColumn<T> extends AbstractColumn<T, String> {

	private IModel<String> label;
	private StaticImage image;
	protected String delete;

	public ButtonColumn(IModel<String> displayModel) {
		super(displayModel);
		label = displayModel;
	}

	public ButtonColumn(IModel<String> displayModel, IModel<String> label, StaticImage image) {
		super(displayModel);
		this.label = label;
		this.image = image;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> components, String s, IModel<T> model) {
		components.add(createButtonPanel(s, model, label == null ? null : label.getObject()));
	}

	protected ButtonPanel createButtonPanel(String id, final IModel<T> model, String displayModel) {
		return new ButtonPanel(id, displayModel, image) {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				ButtonColumn.this.onSubmit(model,target);
			}
		};
	}

	abstract protected void onSubmit(IModel<T> model, AjaxRequestTarget target);

}
