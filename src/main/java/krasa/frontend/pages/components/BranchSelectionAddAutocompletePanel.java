package krasa.frontend.pages.components;

import krasa.backend.async.Callback;
import krasa.backend.domain.SvnFolder;
import krasa.backend.facade.Facade;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BranchSelectionAddAutocompletePanel extends Panel {

    private static final int CHOICES_SIZE = 25;
    @SpringBean
    private Facade facade;
    private static Logger logger = Logger.getLogger(BranchSelectionAddAutocompletePanel.class.getName());
    private AutoCompleteTextField<String> field;

    public BranchSelectionAddAutocompletePanel(String id, final Callback<AjaxRequestTarget> callback) {
        super(id);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setShowListOnFocusGain(true);
        field = new AutoCompleteTextField<String>("ac",
                new Model<String>(""), autoCompleteSettings) {
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
        form.add(field);
        Button button = new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {

                String objectAsString = field.getDefaultModelObjectAsString();
                try {
                    try {
                        facade.addSelectedBranch(objectAsString);
                        field.setModelObject("");
                        ajaxRequestTarget.add(field);
                        callback.process(ajaxRequestTarget);
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                error("fuck");
            }
        };
        form.add(button);

    }


}
