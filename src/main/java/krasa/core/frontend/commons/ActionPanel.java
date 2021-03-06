package krasa.core.frontend.commons;

import krasa.svn.backend.domain.SvnFolder;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Optional;

public abstract class ActionPanel extends Panel {
	public abstract void onClick(Optional<AjaxRequestTarget> target, IModel model, Panel parent);

	public ActionPanel(String id, final IModel model, final Panel parent) {
		super(id, model);
		add(new AjaxFallbackLink<SvnFolder>("link") {
			@Override
			public void onClick(Optional<AjaxRequestTarget> target) {
				ActionPanel.this.onClick(target, model, parent);
			}
		});
	}
}
