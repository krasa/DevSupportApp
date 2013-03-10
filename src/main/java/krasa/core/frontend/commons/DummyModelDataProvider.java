package krasa.core.frontend.commons;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class DummyModelDataProvider<T extends Serializable> implements ISortableDataProvider<T, String> {

	private IModel<List<T>> ts;

	public DummyModelDataProvider(IModel<List<T>> ts) {
		if (ts == null) {
			throw new IllegalArgumentException();
		}
		this.ts = ts;
	}

	public void detach() {
		ts.detach();
	}

	public Iterator<? extends T> iterator(long first, long count) {
		return ts.getObject().iterator();
	}

	public long size() {
		return ts.getObject().size();
	}

	public IModel<T> model(T object) {
		return new Model<T>(object);
	}

	public ISortState getSortState() {
		return new SingleSortState();
	}
}
