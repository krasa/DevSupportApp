package krasa.svn.frontend.component.table;

import java.util.*;

import krasa.svn.backend.domain.SvnFolder;

import krasa.svn.backend.facade.SvnFacade;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.*;

/**
 * @author Vojtech Krasa
 */
public class SortableSelectedBranchesDataProvider implements ISortableDataProvider<SvnFolder, String> {

	@SpringBean
	private SvnFacade facade;
	private String parentPath;
	protected final LoadableDetachableModel<List<SvnFolder>> loadableDetachableModel;

	/**
	 * constructor
	 */
	public SortableSelectedBranchesDataProvider() {
		// set default sort
		loadableDetachableModel = new LoadableDetachableModel<List<SvnFolder>>() {
			@Override
			protected List<SvnFolder> load() {
				return getFacade().getSelectedBranches();
			}
		};
	}

	protected SvnFacade getFacade() {
		if (facade == null) {
			SpringComponentInjector.get().inject(this);
		}
		return facade;
	}

	@Override
	public Iterator<? extends SvnFolder> iterator(long first, long count) {
		return loadableDetachableModel.getObject().iterator();

	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	@Override
	public long size() {
		return loadableDetachableModel.getObject().size();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(Object)
	 */
	@Override
	public IModel<SvnFolder> model(SvnFolder object) {
		return new MyModel(object);
	}

	@Override
	public void detach() {
		loadableDetachableModel.detach();
	}

	@Override
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
