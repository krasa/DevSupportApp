package krasa.merge.frontend.components;

import java.util.Iterator;
import java.util.List;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * @author Vojtech Krasa
 */
public class SortableSvnFolderDataProvider implements ISortableDataProvider<SvnFolder, String> {

	@SpringBean
	private Facade facade;
	private String parentPath;
	protected final LoadableDetachableModel<List<SvnFolder>> loadableDetachableModel;

	public SortableSvnFolderDataProvider(final String parentPath) {
		// set default sort
		// setSort("name", SortOrder.ASCENDING);
		this.parentPath = parentPath;
		loadableDetachableModel = new LoadableDetachableModel<List<SvnFolder>>() {
			@Override
			protected List<SvnFolder> load() {
				return getFacade().getSubDirs(SortableSvnFolderDataProvider.this.parentPath);
			}
		};
	}

	protected Facade getFacade() {
		if (facade == null) {
			SpringComponentInjector.get().inject(this);
		}
		return facade;
	}

	public Iterator<? extends SvnFolder> iterator(long first, long count) {
		return loadableDetachableModel.getObject().iterator();

	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	public long size() {
		return loadableDetachableModel.getObject().size();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
	 */
	public IModel<SvnFolder> model(SvnFolder object) {
		return new MyModel(object);
	}

	public void detach() {
		loadableDetachableModel.detach();
	}

	public ISortState getSortState() {
		return new SingleSortState();
	}

	class MyModel extends LoadableDetachableModel<SvnFolder> {

		Integer id;

		MyModel(SvnFolder object) {
			super(object);
			this.id = object.getId();
		}

		@Override
		protected SvnFolder load() {
			return getFacade().getSvnFolderById(id);
		}
	}

}
