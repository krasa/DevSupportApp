package krasa.core.frontend.commons;

import org.apache.wicket.model.IModel;

public class FishEyeLinkModel implements IModel<String> {
	private long revision;

	public FishEyeLinkModel(long revision) {
		this.revision = revision;
	}

	@Override
	public String getObject() {
		return String.valueOf(revision);
	}

	@Override
	public void setObject(String object) {
	}

	@Override
	public void detach() {

	}
}
