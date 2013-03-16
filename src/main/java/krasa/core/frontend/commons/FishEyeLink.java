package krasa.core.frontend.commons;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.env.Environment;

public class FishEyeLink extends ExternalLink {
	private final IModel<String> href;
	private IModel<String> tooltip;
	@SpringBean
	Environment environment;

	public FishEyeLink(String id, IModel<String> href, IModel<?> label) {
		super(id, href, label);
		this.href = href;
	}

	@Override
	public MarkupContainer setDefaultModel(IModel<?> model) {
		return super.setDefaultModel(wrapBaseUrl(model));
	}

	private IModel<?> wrapBaseUrl(final IModel<?> model) {
		return new Model<String>() {
			@Override
			public String getObject() {
				return environment.getProperty("fisheye.url") + model.getObject();
			}
		};
	}

	public FishEyeLink(String id, IModel<String> href, IModel<String> label, IModel<String> tooltip) {
		super(id, href, label);
		this.href = href;
		this.tooltip = tooltip;
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		if (tooltip != null) {
			tag.put("Title", tooltip.getObject());
		}
	}
}
