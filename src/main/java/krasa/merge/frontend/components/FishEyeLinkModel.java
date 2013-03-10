package krasa.merge.frontend.components;

import org.apache.wicket.model.IModel;

public class FishEyeLinkModel implements IModel<String> {
	private long revision;

	public FishEyeLinkModel(long revision) {
		this.revision = revision;
	}

	public String getObject() {
		return String.valueOf(revision);
	}

	public void setObject(String object) {
	}

	public void detach() {

	}
}
