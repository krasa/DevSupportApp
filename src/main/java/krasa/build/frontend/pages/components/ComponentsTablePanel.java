package krasa.build.frontend.pages.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.MySession;
import krasa.core.frontend.commons.EditablePanel;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.commons.table.BookmarkableColumn;
import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.core.frontend.commons.table.CheckBoxColumn;
import krasa.core.frontend.commons.table.DateColumn;
import krasa.core.frontend.commons.table.DummyModelDataProvider;
import krasa.core.frontend.commons.table.PropertyEditableColumn;
import krasa.core.frontend.components.BasePanel;
import krasa.build.backend.domain.BuildRequest;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vojtech Krasa
 */
public class ComponentsTablePanel extends BasePanel {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private final IModel<Environment> environmentIModel;
	protected Form form;
	protected final FeedbackPanel feedback;
	protected final AjaxButton deploySelectedButton;
	@SpringBean
	private BuildFacade buildFacade;

	public ComponentsTablePanel(String id, final IModel<Environment> environmentIModel) {
		super(id);
		this.environmentIModel = environmentIModel;
		add(feedback = new MyFeedbackPanel("feedback"));
		add(createTable(environmentIModel));
		add(deploySelectedButton = deployButton());
	}

	private AjaxFallbackDefaultDataTable<BuildableComponentDto, String> createTable(IModel<Environment> environmentIModel) {
		return new AjaxFallbackDefaultDataTable<BuildableComponentDto, String>("branches", getColumns(),
				getModel(environmentIModel), 100);
	}

	@Subscribe
	public void receiveMessage(AjaxRequestTarget target, String message) {
		target.add(this);
	}

	private AjaxButton deployButton() {
		return new AjaxButton("deploy") {
			@Override
			protected void onConfigure() {
				Environment env = getEnvironment();
				setEnabled(!MySession.get().getScheduledComponentsByEnvironmentId(env).isEmpty());
				super.onConfigure();
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				try {
					Environment env = getEnvironment();
					Set<Integer> components = MySession.get().getScheduledComponentsByEnvironmentId(env);
					List<BuildableComponent> componentsByEnvironment = buildFacade.getComponentsByEnvironment(env);
					List<BuildableComponent> componentsToBuild = new ArrayList<BuildableComponent>();
					for (BuildableComponent buildableComponent : componentsByEnvironment) {
						if (components.contains(buildableComponent.getId())) {
							componentsToBuild.add(buildableComponent);
						}
					}
					BuildJob job = buildFacade.build(new BuildRequest(componentsToBuild));
					MySession.get().clear(env);
					setResponsePage(new LogPage(LogPage.params(job)));
				} catch (ProcessAlreadyRunning e) {
					info("already building");
					target.add(feedback);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
				super.onError(target, form);
			}
		};
	}

	private DummyModelDataProvider<BuildableComponentDto> getModel(final IModel<Environment> model) {
		return new DummyModelDataProvider<>(new LoadableDetachableModel<List<BuildableComponentDto>>() {
			@Override
			protected List<BuildableComponentDto> load() {
				return BuildableComponentDto.transform(buildFacade.getComponentsByEnvironment(model.getObject()));
			}
		});
	}

	private List<IColumn<BuildableComponentDto, String>> getColumns() {
		final ArrayList<IColumn<BuildableComponentDto, String>> columns = new ArrayList<IColumn<BuildableComponentDto, String>>();
		columns.add(checkBoxColumn());
		columns.add(new BookmarkableColumn<BuildableComponentDto, String>(new Model<String>("name"), "name", "name"));
		columns.add(new PropertyEditableColumn<BuildableComponentDto, String>(new Model<String>("buildMode"), "buildMode",
				"buildMode", 60) {
			@Override
			protected void decoratePanel(final EditablePanel<BuildableComponentDto> panel) {
				super.decoratePanel(panel);
				panel.getTextfield().add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						BuildableComponentDto defaultModelObject = (BuildableComponentDto) panel.getDefaultModelObject();
						buildFacade.saveBuildMode(defaultModelObject.getId(), defaultModelObject.getBuildMode());
					}
				});
			}
		});
		columns.add(new DateColumn<BuildableComponentDto>(new Model<String>("last build"), "lastBuildTime",
				"lastBuildTime"));
		columns.add(new PropertyColumn<BuildableComponentDto, String>(new Model<String>("last build status"), "status", "status"));
		columns.add(buildColumn());
		columns.add(goToProcessColumn());
		columns.add(deleteColumn());
		return columns;
	}

	private CheckBoxColumn<BuildableComponentDto> checkBoxColumn() {
		return new CheckBoxColumn<BuildableComponentDto>(new Model<String>("")) {

			@Override
			public boolean isChecked(IModel<BuildableComponentDto> model) {
				return MySession.get().isQueued(getEnvironment(), model.getObject());
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel,
									IModel<BuildableComponentDto> model) {
				MySession mySession = MySession.get();
				if (booleanIModel.getObject()) {
					mySession.queueComponentToEnvironmentBuild(getEnvironment(), model.getObject());
				} else {
					mySession.removeComponentFromBuild(getEnvironment(), model.getObject());
				}
				target.add(deploySelectedButton);
			}
		};
	}

	private Environment getEnvironment() {
		return environmentIModel.getObject();
	}

	private ButtonColumn<BuildableComponentDto> goToProcessColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<String>("Go to process")) {
			@Override
			public void populateItem(Item<ICellPopulator<BuildableComponentDto>> components, String s,
									 IModel<BuildableComponentDto> model) {
				super.populateItem(components, s, model);
				BuildableComponentDto component = model.getObject();
				components.setEnabled(component.getBuildJobId() != null);
			}

			@Override
			protected void onSubmit(final IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				BuildableComponentDto component = model.getObject();
				setResponsePage(LogPage.class, LogPage.params(component));
			}
		};
	}

	private ButtonColumn<BuildableComponentDto> buildColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<String>("Build")) {
			@Override
			protected void onSubmit(IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				try {
					buildFacade.buildComponent(model.getObject());
					info("Started building");
					target.add(form);
				} catch (ProcessAlreadyRunning e) {
					info("already building");
					target.add(feedback);
				}
			}
		};
	}

	private IColumn<BuildableComponentDto, String> deleteColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<String>("Delete")) {
			@Override
			protected void onSubmit(IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				buildFacade.deleteComponentById(model.getObject().getId());
				MySession.get().removeComponentFromBuild(getEnvironment(), model.getObject());
				target.add(form);
			}
		};
	}

}
