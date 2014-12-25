package krasa.core.frontend.commons.table;

import java.io.Serializable;
import java.util.*;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.*;
import org.apache.wicket.model.*;

public class SortableFilteredModelDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

	private IModel<List<T>> ts;
	private IFilter<T> filter;

	public SortableFilteredModelDataProvider(IModel<List<T>> model, String sort, SortOrder order, IFilter<T> filter) {
		this(model);
		this.filter = filter;
		getSortState().setPropertySortOrder(sort, order);
	}

	class SortableDataProviderComparator implements Comparator<T>, Serializable {

		public int compare(final T o1, final T o2) {
			SortParam<String> sort = getSort();
			if (sort == null) {
				return 0;
			}
			PropertyModel<Comparable> model1 = new PropertyModel<Comparable>(o1, sort.getProperty());
			PropertyModel<Comparable> model2 = new PropertyModel<Comparable>(o2, sort.getProperty());

			int result = model1.getObject().compareTo(model2.getObject());

			if (!sort.isAscending()) {
				result = -result;
			}

			return result;
		}

	}

	private SortableDataProviderComparator comparator = new SortableDataProviderComparator();

	public SortableFilteredModelDataProvider(IModel<List<T>> ts) {
		if (ts == null) {
			throw new IllegalArgumentException();
		}
		this.ts = ts;
	}

	@Override
	public void detach() {
		super.detach();
		ts.detach();
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		List<T> object = ts.getObject();

		List<T> newList = new ArrayList<T>(object);

		Collections.sort(newList, comparator);
		List<T> filtered = filter.filter(newList);
		return filtered.iterator();
	}

	@Override
	public long size() {
		return ts.getObject().size();
	}

	@Override
	public IModel<T> model(T object) {
		return new Model<>(object);
	}

}
