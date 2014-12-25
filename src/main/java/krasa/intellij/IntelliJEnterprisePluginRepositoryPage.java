package krasa.intellij;

import org.apache.wicket.Application;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;

public class IntelliJEnterprisePluginRepositoryPage extends WebPage {

	private boolean stripTags;

	public IntelliJEnterprisePluginRepositoryPage() {
		add(new RepositoryIndexXmlPanel("index"));
		stripTags = Application.get().getMarkupSettings().getStripWicketTags();
	}

	@Override
	protected void onBeforeRender() {
		Application.get().getMarkupSettings().setStripWicketTags(true);
		super.onBeforeRender();
	}

	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		Application.get().getMarkupSettings().setStripWicketTags(stripTags);
	}

	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("xml", "text/xml");
	}

}
