package krasa.build.frontend.pages;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.dto.BuildJobDto;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.dto.Result;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.components.BuildLeftPanel;
import krasa.build.frontend.components.LogModel;
import krasa.build.frontend.components.LogPanel;
import krasa.build.frontend.components.PocessKillButton;
import krasa.build.frontend.components.PocessRerunButton;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPage extends BasePage {

	public static final String COMPONENT_ID = "componentId";
	public static final String ID = "buildJobId";
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected Form form;
	@SpringBean
	protected BuildFacade facade;
	protected PocessKillButton kill;
	protected PocessKillButton kill2;
	private IModel<BuildJob> model;
	private Integer buildJobId;
	private PocessRerunButton rerun1;
	private PocessRerunButton rerun2;

	public LogPage(PageParameters parameters) {
		super(parameters);
		initializeJobId();

		this.model = new LoadableDetachableModel<BuildJob>() {
			@Override
			protected BuildJob load() {
				return facade.getBuildJobById(buildJobId);
			}
		};
		init();

		add(new Behavior() {
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new JavaScriptContentHeaderItem("  setTimeout(function(){\n"
						+ "window.scroll(0,document.body.scrollHeight);\n" + "    }, 100);", null, null));

			}
		});
	}

	public static PageParameters params(BuildableComponentDto component) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(COMPONENT_ID, component.getId());
		return pageParameters;
	}

	public static PageParameters params(BuildJobDto buildJob) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(ID, buildJob.getBuildJobId());
		return pageParameters;
	}

	private void initializeJobId() {
		StringValue stringValue = getPageParameters().get(ID);
		if (!stringValue.isEmpty()) {
			buildJobId = stringValue.toInt();
		} else {
			stringValue = getPageParameters().get(COMPONENT_ID);
			Integer componentId = stringValue.toInteger();
			buildJobId = facade.getBuildJobByComponentId(componentId).getId();
		}
	}

	private void init() {
		if (model.getObject() == null) {
			getFeedbackPanel().error("Process not found");
		}
		add(new Label("info", new AbstractReadOnlyModel<Object>() {
			@Override
			public Object getObject() {
				BuildJob object = model.getObject();
				return object.toString();
			}
		}));
		form = new Form("form");

		form.add(getLogPanel());
		add(form);

		kill = new PocessKillButton("kill", model);
		kill2 = new PocessKillButton("kill2", model);
		form.add(kill);
		form.add(kill2);
		rerun1 = new PocessRerunButton("rerun1", model);
		form.add(rerun1);
		rerun2 = new PocessRerunButton("rerun2", model);
		form.add(rerun2);
	}

	private LogPanel getLogPanel() {
		return new LogPanel("log", new LogModel() {
			@Override
			public boolean isAlive() {
				return model.getObject().isProcessAlive();
			}

			@Override
			public Result getLog() {
				return model.getObject().getLog();
			}

			@Override
			public Result getNextLog(int length) {
				return model.getObject().getNextLog(length);
			}

			@Override
			public boolean exists() {
				return model.getObject() != null;
			}
		}) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(rerun1);
				target.add(rerun2);
				target.add(kill);
				target.add(kill2);
			}
		};
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new BuildLeftPanel(id, null);
	}
}
