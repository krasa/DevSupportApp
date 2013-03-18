package krasa.merge.frontend.component.table;

import java.util.List;

import krasa.merge.backend.facade.Facade;

import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Vojtech Krasa
 */
public class SelectedBranchesModel extends LoadableDetachableModel<List<String>> {
	private Facade facade;

	public SelectedBranchesModel(Facade facade) {
		this.facade = facade;
	}

	@Override
	protected List<String> load() {
		return facade.getSelectedBranchesNames();
	}
}
