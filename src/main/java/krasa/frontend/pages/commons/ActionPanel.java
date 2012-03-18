package krasa.frontend.pages.commons;

import krasa.backend.domain.SvnFolder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class ActionPanel extends Panel {
    public abstract void onClick(AjaxRequestTarget target, IModel model, Panel parent);

    public ActionPanel(String id, final IModel model, final Panel parent) {
        super(id, model);
        add(new AjaxFallbackLink<SvnFolder>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ActionPanel.this.onClick(target, model, parent);
            }
        });
    }
}