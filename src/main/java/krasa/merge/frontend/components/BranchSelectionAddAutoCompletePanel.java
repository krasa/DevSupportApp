package krasa.merge.frontend.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

public abstract class BranchSelectionAddAutoCompletePanel extends Panel {

	private static final int CHOICES_SIZE = 25;
	protected final FeedbackPanel feedback;
	@SpringBean
	private Facade facade;
	protected static Logger logger = Logger.getLogger(BranchSelectionAddAutoCompletePanel.class.getName());
	protected AutoCompleteTextField<String> field;

	public BranchSelectionAddAutoCompletePanel(String id, ResourceModel labelModel) {
		super(id);
		Form form = new Form("form");
		form.setOutputMarkupId(true);
		add(form);
		feedback = new MyFeedbackPanel("feedback");
		form.add(new Label("label", labelModel));
		form.add(feedback);
		AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
		autoCompleteSettings.setShowListOnFocusGain(true);
		field = new AutoCompleteTextField<String>("field", new Model<String>(""), autoCompleteSettings) {
			@Override
			protected Iterator<String> getChoices(String input) {
				if (Strings.isEmpty(input)) {
					List<String> emptyList = Collections.emptyList();
					return emptyList.iterator();
				}
				List<String> choices = new ArrayList<String>();
				for (SvnFolder product : facade.findBranchesByNameLike(input)) {
					String choice = product.getName();

					choices.add(choice);
					if (choices.size() == CHOICES_SIZE) {
						break;
					}
				}
				return choices.iterator();
			}
		};
		field.setRequired(true);
		form.add(field);
		Button button = new AjaxButton("submit") {
			@Override
			protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
				try {
					BranchSelectionAddAutoCompletePanel.this.onSubmit(ajaxRequestTarget);
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				ajaxRequestTarget.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
		};
		form.add(button);

	}

	protected void onSubmit(AjaxRequestTarget target) {
		String objectAsString = field.getDefaultModelObjectAsString();
		field.setModelObject("");
		target.add(field);
		process(target, objectAsString);
	}

	abstract public void process(AjaxRequestTarget target, String objectAsString);

}
