package krasa.build.frontend.pages.components;

import krasa.merge.frontend.component.AddBranchFormPanel;
import krasa.merge.frontend.component.BranchAutoCompletePanel;

import org.apache.wicket.model.ResourceModel;

/**
 * @author Vojtech Krasa
 */
public abstract class AddComponentFormPanel extends AddBranchFormPanel {
	public AddComponentFormPanel(String id) {
		super(id);
	}

	public AddComponentFormPanel(String addBranch, ResourceModel labelModel) {
		super(addBranch, labelModel);
	}

	@Override
	protected BranchAutoCompletePanel createAutoCompletePanel() {
		return new BuildableComponentAutoCompletePanel("autocomplete");
	}
}
