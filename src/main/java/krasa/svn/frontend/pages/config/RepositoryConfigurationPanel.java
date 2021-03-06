package krasa.svn.frontend.pages.config;

import java.util.*;

import krasa.core.backend.domain.GlobalSettings;
import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.components.BasePanel;
import krasa.svn.backend.domain.*;

import krasa.svn.backend.facade.SvnFacade;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class RepositoryConfigurationPanel extends BasePanel {

	protected final IModel<GlobalSettings> globalSettings;
	protected final Form<Repository> form;
	protected final EntityModelWrapper<Repository> repositoryEntityModelWrapper = new EntityModelWrapper<>();
	@SpringBean
	SvnFacade facade;

	public RepositoryConfigurationPanel(String id) {
		super(id);
		form = new Form<>("form", createNewModel());
		form.add(createList());
		form.add(new AjaxButton("add") {

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				facade.saveRepository((Repository) form.getModelObject());
				repositoryEntityModelWrapper.setWrappedModel(newRepository());
				refresh(target);
			}
		});
		form.add(new TextField<String>("url"));
		add(form);
		globalSettings = getGlobalSettings();
	}

	private CompoundPropertyModel<Repository> createNewModel() {
		repositoryEntityModelWrapper.setWrappedModel(newRepository());
		return new CompoundPropertyModel<>(repositoryEntityModelWrapper);
	}

	private AjaxFallbackDefaultDataTable<Repository, String> createList() {
		List<IColumn<Repository, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<Repository, String>(new Model<String>("url"), "url"));
		columns.add(new LabelColumn<Repository>(new Model<String>("default")) {

			@Override
			protected Object getModel(IModel<Repository> rowModel) {
				GlobalSettings object = globalSettings.getObject();

				Repository defaultRepository = object.getDefaultRepository();
				if (defaultRepository == null) {
					return null;
				}
				return defaultRepository.getId().equals(rowModel.getObject().getId());
			}
		});
		// columns.add(new ButtonColumn<Repository>(new Model<String>("edit")) {
		// @Override
		// protected void onSubmit(IModel<Repository> model, AjaxRequestTarget target) {
		// repositoryEntityModelWrapper.setWrappedModel(model);
		// target.add(form);
		// }
		// });

		columns.add(new DropDownChoiceColumn<Repository, String>(new Model<String>("repositoryStructure"),
				"repositoryStructureAsString") {

			@Override
			protected IModel<List<String>> getDisplayModel(IModel<Repository> rowModel) {
				return new AbstractReadOnlyModel<List<String>>() {

					@Override
					public List<String> getObject() {
						RepositoryStructure[] values = RepositoryStructure.values();
						ArrayList<String> strings = new ArrayList<>();
						for (RepositoryStructure value : values) {
							strings.add(value.name());
						}
						return strings;
					}
				};
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target, PropertyModel<String> itemModel) {
				Object innermostModelOrObject = itemModel.getInnermostModelOrObject();
				facade.saveRepository((Repository) innermostModelOrObject);
			}
		});

		columns.add(new ButtonColumn<Repository>(new Model<String>("delete")) {

			@Override
			protected void onSubmit(IModel<Repository> model, AjaxRequestTarget target) {
				facade.deleteRepository(model.getObject().getId());
				refresh(target);
			}
		});

		DummyModelDataProvider<Repository> provider = new DummyModelDataProvider<>(getRepositoriesModel());
		AjaxFallbackDefaultDataTable<Repository, String> repositories = new AjaxFallbackDefaultDataTable<>(
				"repositories", columns, provider, 100);
		return repositories;
	}

	private void refresh(AjaxRequestTarget target) {
		target.add(RepositoryConfigurationPanel.this);
	}

	private IModel<List<Repository>> getRepositoriesModel() {
		return new LoadableDetachableModel<List<Repository>>() {

			@Override
			protected List<Repository> load() {
				return facade.getAllRepositories();
			}
		};
	}

	public IModel<GlobalSettings> getGlobalSettings() {
		return new LoadableDetachableModel<GlobalSettings>() {

			@Override
			protected GlobalSettings load() {
				return facade.getGlobalSettings();
			}
		};
	}

	private Model<Repository> newRepository() {
		return new Model<>(new Repository());
	}
}
