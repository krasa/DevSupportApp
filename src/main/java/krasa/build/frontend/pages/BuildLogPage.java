package krasa.build.frontend.pages;

import java.io.*;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.dto.*;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.components.*;
import krasa.core.frontend.pages.*;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.*;

public class BuildLogPage extends BasePage {

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

	public BuildLogPage(PageParameters parameters) {
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
		pageParameters.add(COMPONENT_ID, component.getComponentId());
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
		queue(new Label("info", new AbstractReadOnlyModel<Object>() {

			@Override
			public Object getObject() {
				BuildJob object = model.getObject();
				return object.toString();
			}
		}));
		form = new Form("form");

		form.add(getLogPanel());
		queue(form);

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
				BuildJob buildJob = model.getObject();
				return buildJob.isProcessAlive();
			}

			@Override
			public Result getLog() {
				BuildJob buildJob = model.getObject();
				File logFileByName = FileSystemLogUtils.getLogFileByName(buildJob.getLogFileName());
				return new Result(FileSystemLogUtils.readFile(logFileByName));
			}

			@Override
			public Result getNextLog(int offset) {
				BuildJob buildJob = model.getObject();

				File logFileByName = FileSystemLogUtils.getLogFileByName(buildJob.getLogFileName());
				try (BufferedReader reader = new BufferedReader(new FileReader(logFileByName))) {
					reader.skip(offset);
					String s = IOUtils.toString(reader);
					return new Result(s.length() + offset, s);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			@Override
			public boolean exists() {
				BuildJob buildJob = model.getObject();
				return buildJob != null;
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
		return new BuildLeftPanel(id);
	}
}
