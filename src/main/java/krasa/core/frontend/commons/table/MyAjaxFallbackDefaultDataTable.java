package krasa.core.frontend.commons.table;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.*;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public class MyAjaxFallbackDefaultDataTable<T, S> extends AjaxFallbackDefaultDataTable<T, S> {

	private final List<? extends IColumn<T, S>> iColumns;

	/**
	 * Constructor
	 *
	 * @param id
	 *            component id
	 * @param iColumns
	 *            list of columns
	 * @param dataProvider
	 *            data provider
	 * @param rowsPerPage
	 */
	public MyAjaxFallbackDefaultDataTable(String id, List<? extends IColumn<T, S>> iColumns,
			ISortableDataProvider<T, S> dataProvider, int rowsPerPage) {
		super(id, iColumns, dataProvider, rowsPerPage);
		this.iColumns = iColumns;
	}

	@Override
	protected DataGridView<T> newDataGridView(String id, List<? extends IColumn<T, S>> iColumns,
			IDataProvider<T> dataProvider) {
		return new DefaultDataGridView(id, iColumns, dataProvider);
	}

	private class DefaultDataGridView extends DataGridView<T> {

		public DefaultDataGridView(String id, List<? extends IColumn<T, S>> columns, IDataProvider<T> dataProvider) {
			super(id, columns, dataProvider);
		}

		@Override
		protected IItemFactory<T> newItemFactory() {
			return new IItemFactory<T>() {

				@Override
				public Item<T> newItem(int index, IModel<T> model) {
					T object = model.getObject();
					String id;
					if (object instanceof CustomIdTableItem) {
						id = ((CustomIdTableItem) object).getRowId();
					} else {
						id = DefaultDataGridView.this.newChildId();
					}
					Item<T> item = DefaultDataGridView.this.newItem(id, index, model);
					DefaultDataGridView.this.populateItem(item);
					return item;
				}
			};
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Item newCellItem(final String id, final int index, final IModel model) {
			Item item = MyAjaxFallbackDefaultDataTable.this.newCellItem(id, index, model);
			final IColumn<T, S> column = MyAjaxFallbackDefaultDataTable.this.iColumns.get(index);
			if (column instanceof IStyledColumn) {
				item.add(new CssAttributeBehavior() {

					private static final long serialVersionUID = 1L;

					@Override
					protected String getCssClass() {
						return ((IStyledColumn<T, S>) column).getCssClass();
					}
				});
			}
			return item;
		}

		@Override
		protected Item<T> newRowItem(final String id, final int index, final IModel<T> model) {
			return MyAjaxFallbackDefaultDataTable.this.newRowItem(id, index, model);
		}
	}

	static abstract class CssAttributeBehavior extends Behavior {

		private static final long serialVersionUID = 1L;

		protected abstract String getCssClass();

		/**
		 * @see Behavior#onComponentTag(org.apache.wicket.Component, org.apache.wicket.markup.ComponentTag)
		 */
		@Override
		public void onComponentTag(final Component component, final ComponentTag tag) {
			String className = getCssClass();
			if (!Strings.isEmpty(className)) {
				tag.append("class", className, " ");
			}
		}
	}

}
