package krasa.core.frontend.commons.table;

import java.io.Serializable;
import java.util.*;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.*;

public class DummyModelDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

	private IModel<List<T>> ts;

	public DummyModelDataProvider(IModel<List<T>> ts) {
		if (ts == null) {
			throw new IllegalArgumentException();
		}
		this.ts = ts;
	}

	@Override
	public void detach() {
		ts.detach();
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		return ts.getObject().iterator();
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
