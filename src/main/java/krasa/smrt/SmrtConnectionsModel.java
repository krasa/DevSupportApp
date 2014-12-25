package krasa.smrt;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

public class SmrtConnectionsModel extends LoadableDetachableModel<List<SmrtConnection>> {

	private SmrtMainPage components;

	public SmrtConnectionsModel(SmrtMainPage components) {
		this.components = components;
	}

	@Override
	protected List<SmrtConnection> load() {
		return components.smrtMonitoring.getSmrtConnections();
	}
}
