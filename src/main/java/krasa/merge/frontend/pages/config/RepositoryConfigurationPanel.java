package krasa.merge.frontend.pages.config;

import java.util.List;

import krasa.core.frontend.commons.EntityModelWrapper;
import krasa.core.frontend.components.BasePanel;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class RepositoryConfigurationPanel extends BasePanel {
	@SpringBean
	Facade facade;

	protected final Form<Repository> form;
	protected final EntityModelWrapper<Repository> repositoryEntityModelWrapper = new EntityModelWrapper<>();

	public RepositoryConfigurationPanel(String id) {
		super(id);
		add(createList());
		form = new Form<>("form", createNewModel());
		form.add(new AjaxButton("delete") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				facade.deleteRepository(repositoryEntityModelWrapper.getId());
				repositoryEntityModelWrapper.setWrappedModel(newRepository());
				target.add(RepositoryConfigurationPanel.this);
			}

			@Override
			protected void onConfigure() {
				this.setVisible(repositoryEntityModelWrapper.getId() != null);
				super.onConfigure();
			}
		});
		form.add(new AjaxButton("save") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				facade.saveRepository((Repository) form.getModelObject());
				repositoryEntityModelWrapper.setWrappedModel(newRepository());
				target.add(RepositoryConfigurationPanel.this);
			}
		});
		form.add(new TextField<String>("url"));
		add(form);
	}

	private CompoundPropertyModel<Repository> createNewModel() {
		repositoryEntityModelWrapper.setWrappedModel(newRepository());
		return new CompoundPropertyModel<>(repositoryEntityModelWrapper);
	}

	private PropertyListView<Repository> createList() {
		return new PropertyListView<Repository>("list", getRepositoriesModel()) {
			@Override
			protected void populateItem(final ListItem<Repository> item) {
				item.add(new Label("url"));
				item.add(new AjaxLink("edit") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						repositoryEntityModelWrapper.setWrappedModel(item.getModel());
						target.add(form);
					}
				});
			}
		};
	}

	private LoadableDetachableModel<List<? extends Repository>> getRepositoriesModel() {
		return new LoadableDetachableModel<List<? extends Repository>>() {
			@Override
			protected List<? extends Repository> load() {
				return facade.getAllRepositories();
			}
		};
	}

	private Model<Repository> newRepository() {
		return new Model<>(new Repository());
	}
}
