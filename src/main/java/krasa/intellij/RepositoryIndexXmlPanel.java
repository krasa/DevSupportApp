package krasa.intellij;

import krasa.core.frontend.*;
import krasa.core.frontend.components.BasePanel;
import krasa.core.frontend.utils.Strings;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.request.resource.SharedResourceReference;

/**
 * @author V1ojtech Krasa
 */
public class RepositoryIndexXmlPanel extends BasePanel {
	public RepositoryIndexXmlPanel(String id) {
		super(id);
		final String absoluteUrl = UrlUtils.getAbsoluteUrl(new SharedResourceReference(
				WicketApplication.INTELLIJ_PLUGIN_REPO_RESOURCES));

		ListView<PluginDefinition> components = new ListView<PluginDefinition>("list", new IntelliJPluginsModel()) {

			@Override
			protected void populateItem(ListItem<PluginDefinition> item) {
				PluginDefinition modelObject = item.getModelObject();
				Label plugin = new Label("plugin") {
					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
					}
				};
				String name = modelObject.getFileName();
				plugin.add(new AttributeModifier("id", Strings.cutExtension(name)));
				plugin.add(new AttributeModifier("url", absoluteUrl + "/" + modelObject.getFileName()));
				plugin.add(new AttributeModifier("version", modelObject.getVersion()));
				item.add(plugin);
			}
		};
		components.setOutputMarkupPlaceholderTag(true);
		add(components);
	}

}
