package krasa.core.frontend;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;

public class Ajax {

	public static AjaxRequestTarget getAjaxRequestTarget() {
		return RequestCycle.get().find(AjaxRequestTarget.class);
	}
}
