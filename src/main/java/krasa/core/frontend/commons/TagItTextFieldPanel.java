package krasa.core.frontend.commons;

import krasa.merge.backend.facade.Facade;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class TagItTextFieldPanel extends Panel {
	@SpringBean
	private Facade facade;

	public TagItTextFieldPanel(String componentId, String path, Model<String> of) {
		super(componentId, of);
		// add(new TagItTextField<SvnFolder>("input", of) {
		//
		// @Override
		// protected Iterable<SvnFolder> getChoices(String input) {
		// return facade.getSuggestions(path, input);
		// }
		// });

	}

	public TagItTextFieldPanel(String id) {
		super(id);

	}
}
