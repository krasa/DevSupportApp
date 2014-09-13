package krasa.merge.frontend.pages.mergeinfo;

import krasa.core.frontend.components.BasePanel;
import krasa.merge.frontend.component.SvnProjectsLeftMenuPanel;
import krasa.merge.frontend.component.merge.MergesPanel;

/**
 * @author Vojtech Krasa
 */
public class MergeLeftPanel extends BasePanel {

	public MergeLeftPanel(String id) {
		super(id);
		add(new MergesPanel("LastMergesPanel"));
		add(new SvnProjectsLeftMenuPanel("SvnProjectsLeftMenuPanel"));
	}

}
