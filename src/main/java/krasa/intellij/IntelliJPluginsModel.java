package krasa.intellij;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Vojtech Krasa
 */
class IntelliJPluginsModel extends LoadableDetachableModel<List<PluginDefinition>> {

	@Override
	protected List<PluginDefinition> load() {
		return IntelliJUtils.getPluginsIndex().getPluginDefinitions();
	}
}
