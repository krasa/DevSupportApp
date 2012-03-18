package krasa.frontend.pages.report;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Vojtech Krasa
 */
public class ReportResultPanel extends Panel {

    public ReportResultPanel(String id) {
        super(id);
    }

    public ReportResultPanel(String id, IModel<?> model) {
        super(id, model);
    }
}
