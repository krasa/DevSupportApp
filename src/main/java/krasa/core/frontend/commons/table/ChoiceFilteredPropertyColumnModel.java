package krasa.core.frontend.commons.table;

import java.util.*;

import org.apache.wicket.model.*;

public class ChoiceFilteredPropertyColumnModel<T extends Comparable, R> implements IModel<List<? extends T>> {

	private final IModel<List<R>> model;
	private String expression;
	protected ArrayList<T> list;

	public ChoiceFilteredPropertyColumnModel(IModel<List<R>> model, String expression) {
		this.model = model;
		this.expression = expression;
	}

	@SuppressWarnings("unchecked")
	public void reload() {
		Set<T> s = new HashSet<>();
		for (R smrtConnection : model.getObject()) {
			if (filter(smrtConnection)) {
				Object object = new PropertyModel(smrtConnection, expression).getObject();
				s.add((T) object);
			}
		}
		list = new ArrayList<>(s);
		Collections.sort(list);
	}

	protected boolean filter(R smrtConnection) {
		return true;
	}

	@Override
	public List<T> getObject() {
		if (list == null) {
			reload();
		}
		return list;
	}

	@Override
	public void setObject(List<? extends T> object) {

	}

	@Override
	public void detach() {
		model.detach();
	}
}
