package krasa.intellij;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.*;

/**
 * @author Vojtech Krasa
 */
@XmlRootElement(name = "plugins")
public class PluginsIndex {

	private List<PluginDefinition> pluginDefinitions = new ArrayList<>();

	public List<PluginDefinition> getPluginDefinitions() {
		return pluginDefinitions;
	}

	public void setPluginDefinitions(List<PluginDefinition> pluginDefinitions) {
		this.pluginDefinitions = pluginDefinitions;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public void add(PluginDefinition pluginDefinition) {
		for (Iterator<PluginDefinition> iterator = pluginDefinitions.iterator(); iterator.hasNext();) {
			PluginDefinition definition = iterator.next();
			if (definition.getId().equals(pluginDefinition.getId())
					&& definition.getVersion().equals(pluginDefinition.getVersion())) {
				iterator.remove();
			}
		}
		pluginDefinitions.add(pluginDefinition);
	}
}
