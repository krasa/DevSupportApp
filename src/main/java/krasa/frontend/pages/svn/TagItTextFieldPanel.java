package krasa.frontend.pages.svn;

import krasa.backend.facade.Facade;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.tagit.TagItTextField;

/**
 * @author Vojtech Krasa
 */
public class TagItTextFieldPanel extends Panel {
    @SpringBean
    private Facade facade;

    public TagItTextFieldPanel(String componentId, final String path, final Model<String> of) {
        super(componentId, of);
        add(new TagItTextField<String>("input", of) {

            @Override
            protected Iterable<String> getChoices(String input) {
                return facade.getSuggestions(path, input);
            }
        });

    }

    public TagItTextFieldPanel(String id) {
        super(id);

    }
}
