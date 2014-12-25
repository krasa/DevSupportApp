package krasa.merge.frontend.component;

import java.io.Serializable;
import java.util.*;

import krasa.core.frontend.MySession;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class ProfileDropDownPanel extends Panel {

	@SpringBean
	private Facade facade;

	public ProfileDropDownPanel(String id) {
		super(id);

		Form form = new Form("form");
		add(form);

		form.add(dropDownChoice());
	}

	private DropDownChoice dropDownChoice() {
		List<Profile> profiles = facade.getProfiles();
		Model<ProfileDropDownChoiceItem> selected = new Model<>();
		List<ProfileDropDownChoiceItem> choiceItems = convert(profiles, selected, getMySession().getCurrentProfileId());

		final DropDownChoice<ProfileDropDownChoiceItem> profilesDDC = new DropDownChoice<>("profiles", selected,
				choiceItems, new ChoiceRenderer<ProfileDropDownChoiceItem>("name", "id"));
		// Add Ajax Behaviour...
		profilesDDC.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				ProfileDropDownChoiceItem modelValue1 = profilesDDC.getModelObject();
				if (modelValue1 != null) {
					getMySession().setCurrentProfile(modelValue1.getId());
					Page page = getPage();
					setResponsePage(page.getClass(), page.getPageParameters());
				}

			}
		});
		profilesDDC.setNullValid(false);
		return profilesDDC;
	}

	private List<ProfileDropDownChoiceItem> convert(List<Profile> profiles, Model<ProfileDropDownChoiceItem> selected,
			Integer current) {
		ArrayList<ProfileDropDownChoiceItem> profileDropDownChoiceItems = new ArrayList<>();
		for (Profile profile : profiles) {
			ProfileDropDownChoiceItem e = new ProfileDropDownChoiceItem(profile);
			profileDropDownChoiceItems.add(e);
			if (profile.getId().equals(current)) {
				selected.setObject(e);
			}
		}
		return profileDropDownChoiceItems;
	}

	private MySession getMySession() {
		return MySession.get();
	}

	class ProfileDropDownChoiceItem implements Serializable {

		String name;
		Integer id;

		public ProfileDropDownChoiceItem(Profile profile) {
			id = profile.getId();
			name = profile.getName();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}

}
