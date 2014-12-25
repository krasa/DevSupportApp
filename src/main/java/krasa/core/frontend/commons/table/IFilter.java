package krasa.core.frontend.commons.table;

import java.util.List;

public interface IFilter<T> {

	List<T> filter(List<T> newList);
}
