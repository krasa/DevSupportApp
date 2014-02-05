package krasa.merge.frontend.component.table;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

/**
 * @author Vojtech Krasa
 */
public class DiffModalWindow extends ModalWindow {
	private static final long serialVersionUID = 1L;

	public DiffModalWindow(String id) {
		super(id);
	}

	public DiffModalWindow(String id, IModel<?> model) {
		super(id, model);
	}


	@Override
	protected CharSequence getShowJavaScript() {
		// Hack in some JS to remove the onMove handlers
		StringBuffer showJS = new StringBuffer();
		showJS.append((String) super.getShowJavaScript());
		showJS.append("SyntaxHighlighter.highlight();");
		return showJS.toString();

	}
}