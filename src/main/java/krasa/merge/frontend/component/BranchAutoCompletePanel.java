package krasa.merge.frontend.component;

import java.util.*;
import java.util.logging.Logger;

import krasa.merge.backend.domain.Displayable;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

public class BranchAutoCompletePanel extends Panel {

	private static final int CHOICES_SIZE = 25;
	@SpringBean
	private Facade facade;
	protected static Logger logger = Logger.getLogger(BranchAutoCompletePanel.class.getName());
	protected AutoCompleteTextField<String> field;

	public BranchAutoCompletePanel(String id) {
		super(id);
		add(field = createAutoCompleteTextField());
	}

	private AutoCompleteSettings createSettings() {
		AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
		autoCompleteSettings.setShowListOnFocusGain(true);
		return autoCompleteSettings;
	}

	private AutoCompleteTextField<String> createAutoCompleteTextField() {
		AutoCompleteTextField<String> components = new AutoCompleteTextField<String>("field", new Model<>(""),
				createSettings()) {

			@Override
			protected Iterator<String> getChoices(String input) {
				if (Strings.isEmpty(input)) {
					List<String> emptyList = Collections.emptyList();
					return emptyList.iterator();
				}
				List<String> choices = new ArrayList<>();
				for (Displayable product : getMatching(input)) {
					String choice = product.getDisplayableText();

					choices.add(choice);
					if (choices.size() == CHOICES_SIZE) {
						break;
					}
				}
				return choices.iterator();
			}
		};
		components.setRequired(true);
		return components;
	}

	protected List<Displayable> getMatching(String input) {
		return facade.findBranchesByNameLikeAsDisplayable(input);
	}

	public void resetFieldValue(AjaxRequestTarget target) {
		field.setModelObject("");
		target.add(field);
	}

	public String getFieldValue() {
		return field.getDefaultModelObjectAsString();
	}

	public AutoCompleteTextField<String> getField() {
		return field;
	}
}
