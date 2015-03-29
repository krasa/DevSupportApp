package krasa.build.frontend.components;

import java.util.List;

import krasa.build.backend.facade.BuildFacade;
import krasa.merge.backend.domain.Displayable;
import krasa.merge.frontend.component.BranchAutoCompletePanel;

import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class BranchesAndTagsAutoCompletePanel extends BranchAutoCompletePanel {

	@SpringBean
	protected BuildFacade buildFacade;

	public BranchesAndTagsAutoCompletePanel(String id) {
		super(id);
	}

	@Override
	protected List<Displayable> getMatching(String input) {
		return buildFacade.getMatchingBranchesAndTags(input);
	}
}
