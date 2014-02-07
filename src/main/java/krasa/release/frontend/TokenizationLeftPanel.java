package krasa.release.frontend;

import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.components.BasePanel;

import org.apache.wicket.spring.injection.annot.SpringBean;

public class TokenizationLeftPanel extends BasePanel {

	@SpringBean
	private BuildFacade facade;

	public TokenizationLeftPanel(String id) {
		super(id);
		add(new TokenizationJobsLeftPanel("jobs"));
	}

}
