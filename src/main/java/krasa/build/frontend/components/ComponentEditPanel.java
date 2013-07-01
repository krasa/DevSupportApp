package krasa.build.frontend.components;

import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.frontend.components.BaseFormPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ComponentEditPanel extends BaseFormPanel<BuildableComponentDto> {

	@SpringBean
	BuildFacade buildFacade;

	public ComponentEditPanel(String contentId, IModel<BuildableComponentDto> model) {
		super(contentId, model);
	}

	@Override
	protected void initForm(Form form) {
		PropertyModel<String> name = new PropertyModel<>(getFormPanelModel(), "name");
		TextArea<String> nameField = new TextArea<>("name", name);
		nameField.setRequired(true);

		form.add(nameField);
		form.add(new TextArea<>("buildMode", new PropertyModel<>(getFormPanelModel(), "buildMode")));
		form.add(new AjaxButton("save") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				BuildableComponentDto buildableComponentDto = buildFacade.editBuildableComponent(getFormPanelModel().getObject());
				ComponentEditPanel.this.onSubmit(target, buildableComponentDto);
			}
		});
	}

	public void onSubmit(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
	};

}
