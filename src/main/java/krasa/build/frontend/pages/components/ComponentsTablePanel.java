package krasa.build.frontend.pages.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import krasa.build.backend.domain.ComponentBuild;
import krasa.build.backend.domain.Environment;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.MySession;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.commons.table.BookmarkableColumn;
import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.core.frontend.commons.table.CheckBoxColumn;
import krasa.core.frontend.commons.table.DateColumn;
import krasa.core.frontend.commons.table.DummyModelDataProvider;
import krasa.core.frontend.components.BasePanel;
import krasa.merge.backend.dto.BuildRequest;

import org.apache.wicket.ajax.AjaxRequestTarget;
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

	private AjaxFallbackDefaultDataTable<ComponentBuild, String> createTable(IModel<Environment> environmentIModel) {
		return new AjaxFallbackDefaultDataTable<ComponentBuild, String>("branches", getColumns(),
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
				setEnabled(!MySession.get().getScheduledBranchesByEnvironmentId(env).isEmpty());
				super.onConfigure();
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				try {
					Environment env = getEnvironment();
					List<String> scheduledBranchesByEnvironmentId = MySession.get().getScheduledBranchesByEnvironmentId(
							env);
					ProcessAdapter deploy = buildFacade.build(new BuildRequest(scheduledBranchesByEnvironmentId,
							env.getName()));
					MySession.get().clear(env);
					setResponsePage(new LogPage(deploy));
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

	private DummyModelDataProvider<ComponentBuild> getModel(final IModel<Environment> model) {
		return new DummyModelDataProvider<ComponentBuild>(new LoadableDetachableModel<List<ComponentBuild>>() {
			@Override
			protected List<ComponentBuild> load() {
				return buildFacade.getBranchBuilds(model.getObject());
			}
		});
	}

	private List<IColumn<ComponentBuild, String>> getColumns() {
		final ArrayList<IColumn<ComponentBuild, String>> columns = new ArrayList<IColumn<ComponentBuild, String>>();
		columns.add(checkBoxColumn());
		columns.add(new BookmarkableColumn<ComponentBuild, String>(new Model<String>("name"), "name", "name"));
		columns.add(new DateColumn<ComponentBuild>(new Model<String>("last successful build"), "lastSuccessBuild",
				"lastSuccessBuild"));
		columns.add(new PropertyColumn<ComponentBuild, String>(new Model<String>("status"), "status", "status"));
		columns.add(buildColumn());
		columns.add(goToProcessColumn());
		columns.add(deleteColumn());
		return columns;
	}

	private CheckBoxColumn<ComponentBuild> checkBoxColumn() {
		return new CheckBoxColumn<ComponentBuild>(new Model<String>("")) {

			@Override
			public boolean isChecked(IModel<ComponentBuild> model) {
				return MySession.get().isQueued(getEnvironment(), model.getObject().getName());
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target, IModel<Boolean> booleanIModel,
					IModel<ComponentBuild> model) {
				MySession mySession = MySession.get();
				if (booleanIModel.getObject()) {
					mySession.queueBranchToEnvironmentBuild(getEnvironment(), model.getObject().getName());
				} else {
					mySession.removeBranchFromBuild(getEnvironment(), model.getObject().getName());
				}
				target.add(deploySelectedButton);
			}
		};
	}

	private Environment getEnvironment() {
		return environmentIModel.getObject();
	}

	private ButtonColumn<ComponentBuild> goToProcessColumn() {
		return new ButtonColumn<ComponentBuild>(new Model<String>("Go to process")) {
			@Override
			public void populateItem(Item<ICellPopulator<ComponentBuild>> components, String s,
					IModel<ComponentBuild> model) {
				super.populateItem(components, s, model);
				String componentName = model.getObject().getName();
				ProcessAdapter refresh = buildFacade.refresh(getDeploymentRequest(componentName));
				components.setEnabled(refresh != null);
			}

			@Override
			protected void onSubmit(final IModel<ComponentBuild> model, AjaxRequestTarget target, Form<?> form) {
				setResponsePage(new LogPage(environmentIModel, new LoadableDetachableModel<String>() {

					@Override
					protected String load() {
						return model.getObject().getName();
					}
				}));
			}
		};
	}

	private ButtonColumn<ComponentBuild> buildColumn() {
		return new ButtonColumn<ComponentBuild>(new Model<String>("Build")) {
			@Override
			protected void onSubmit(IModel<ComponentBuild> model, AjaxRequestTarget target, Form<?> form) {
				String componentName = model.getObject().getName();
				try {
					buildFacade.build(getDeploymentRequest(componentName));
					target.add(form);
				} catch (ProcessAlreadyRunning e) {
					info("already building");
					target.add(feedback);
				}
			}
		};
	}

	private IColumn<ComponentBuild, String> deleteColumn() {
		return new ButtonColumn<ComponentBuild>(new Model<String>("Delete")) {
			@Override
			protected void onSubmit(IModel<ComponentBuild> model, AjaxRequestTarget target, Form<?> form) {
				buildFacade.deleteComponent(getEnvironment(), model.getObject());
				target.add(form);
			}
		};
	}

	private BuildRequest getDeploymentRequest(String componentName) {
		return new BuildRequest(Arrays.asList(componentName), getEnvironment().getName());
	}

}
