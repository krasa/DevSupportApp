package krasa.intellij;

import krasa.core.frontend.*;
import krasa.core.frontend.components.BasePanel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.request.resource.SharedResourceReference;

/**
 * @author Vojtech Krasa
 */
public class RepositoryIndexXmlPanel extends BasePanel {

	// todo
	public static final String BASE_URL = "http://vojtitko.tmdev/intellijPlugin";

	public RepositoryIndexXmlPanel(String id) {
		super(id);
		// returns ip+port :(
		final String absoluteUrl = UrlUtils.getAbsoluteUrl(new SharedResourceReference(
				WicketApplication.INTELLIJ_PLUGIN_REPO_RESOURCES));

		ListView<PluginDefinition> components = new ListView<PluginDefinition>("plugin", new IntelliJPluginsModel()) {

			@Override
			protected void populateItem(ListItem<PluginDefinition> item) {
				PluginDefinition modelObject = item.getModelObject();
				item.add(new AttributeModifier("id", modelObject.getId()));
				item.add(new AttributeModifier("url", BASE_URL + "/" + modelObject.getFileName()));
				item.add(new AttributeModifier("version", modelObject.getVersion()));
			}
		};
		components.setOutputMarkupPlaceholderTag(true);
		add(components);
	}

}
