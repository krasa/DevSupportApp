package krasa.svn.frontend.component.table;

import java.util.List;

import krasa.svn.backend.facade.SvnFacade;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Vojtech Krasa
 */
public class SelectedBranchesModel extends LoadableDetachableModel<List<String>> {
	private SvnFacade facade;

	public SelectedBranchesModel(SvnFacade facade) {
		this.facade = facade;
	}

	@Override
	protected List<String> load() {
		return facade.getSelectedBranchesNames();
	}
}
