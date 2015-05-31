package krasa.svn.frontend.pages.mergeinfo;

import krasa.core.frontend.components.BasePanel;
import krasa.svn.frontend.component.SvnProjectsLeftMenuPanel;
import krasa.svn.frontend.component.merge.MergesPanel;

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
