package krasa.core.frontend.commons;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class MyFeedbackPanel extends FeedbackPanel {
	public MyFeedbackPanel(String id) {
		super(id);
	}

	public MyFeedbackPanel(String id, IFeedbackMessageFilter filter) {
		super(id, filter);
	}

	@Override
	protected void onInitialize() {
		setOutputMarkupId(true);
		super.onInitialize();
	}
}
