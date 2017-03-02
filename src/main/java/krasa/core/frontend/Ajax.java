package krasa.core.frontend;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.Optional;

public class Ajax {

	public static AjaxRequestTarget getAjaxRequestTarget() {
		Optional<AjaxRequestTarget> t = RequestCycle.get().find(AjaxRequestTarget.class);
		if (t.isPresent()) {
			return t.get();
		}
		return null;
	}
}
