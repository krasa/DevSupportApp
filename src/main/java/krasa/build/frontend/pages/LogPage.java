package krasa.build.frontend.pages;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.components.BuildLeftPanel;
import krasa.build.frontend.pages.components.LogPanel;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.Component;
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
	private IModel<BuildJob> model;
	@SpringBean
	protected BuildFacade facade;
	protected BuildRequest buildRequest;
	private Integer buildJobId;

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
		form.add(new LogPanel("log", model));
		add(form);
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new BuildLeftPanel(id, null);
	}

	public static PageParameters params(BuildableComponentDto component) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(COMPONENT_ID, component.getId());
		return pageParameters;
	}

	public static PageParameters params(BuildJob buildJob) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(ID, buildJob.getId());
		return pageParameters;
	}
}
