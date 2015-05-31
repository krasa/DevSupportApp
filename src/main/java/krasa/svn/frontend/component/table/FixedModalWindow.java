package krasa.svn.frontend.component.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * @author Vojtech Krasa
 */
public class FixedModalWindow extends ModalWindow {

	private static final long serialVersionUID = 1L;

	public FixedModalWindow(String id) {
		super(id);
		setInitialWidth(1000);
		setInitialHeight(1000);
		showUnloadConfirmation(false);
		setCloseButtonCallback(new CloseButtonCallback() {

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				target.appendJavaScript("$('body').css('overflow','auto');");
				return true;
			}
		});
	}

	@Override
	protected CharSequence getShowJavaScript() {
		// Hack in some JS to remove the onMove handlers
		StringBuilder showJS = new StringBuilder();
		showJS.append((String) super.getShowJavaScript());
		showJS.append("$('body').css('overflow','hidden');");
		return showJS.toString();
	}

	@Override
	protected String getCloseJavacript() {
		StringBuilder closeJS = new StringBuilder();
		closeJS.append(super.getCloseJavacript());
		closeJS.append("$('body').css('overflow','auto');");
		return closeJS.toString();
	}
}
