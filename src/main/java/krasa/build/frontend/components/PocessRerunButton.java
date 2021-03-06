package krasa.build.frontend.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.backend.facade.UsernameException;
import krasa.build.frontend.pages.BuildLogPage;
import krasa.core.frontend.pages.BasePage;

public class PocessRerunButton extends AjaxButton {

	private IModel<BuildJob> model;

	@SpringBean
	protected BuildFacade facade;

	public PocessRerunButton(String id, IModel<BuildJob> model) {
		super(id, new Model<>("Rerun"));
		this.model = model;
	}

	@Override
	protected void onConfigure() {
		BuildJob object = model.getObject();
		if (object != null) {
			this.setEnabled(!object.isProcessAlive());
		}
		super.onConfigure();
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		BuildJob object = model.getObject();
		BuildableComponentDto transform = BuildableComponentDto.transform(object.getBuildableComponent());
		BuildableComponentDto build = null;
		try {
			build = facade.buildComponent(transform);
			this.setEnabled(false);
			target.add(this);
			setResponsePage(BuildLogPage.class, BuildLogPage.params(build));
		} catch (UsernameException e) {
			error(e.getMessage());
			target.add(((BasePage) this.getPage()).getFeedbackPanel());
			target.appendJavaScript("alert('" + e.getMessage() + "');");
		}

	}
}
