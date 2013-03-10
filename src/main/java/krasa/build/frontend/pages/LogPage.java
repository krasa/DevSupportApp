package krasa.build.frontend.pages;

import java.util.Arrays;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.components.LogPanel;
import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.dto.BuildRequest;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPage extends BasePage {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected Form form;
	private IModel<ProcessAdapter> model;
	@SpringBean
	protected BuildFacade facade;
	protected BuildRequest request;
	protected BuildRequest buildRequest;

	public LogPage(ProcessAdapter model) {
		buildRequest = model.getRequest();
		this.model = new LoadableDetachableModel<ProcessAdapter>() {
			@Override
			protected ProcessAdapter load() {
				return facade.refresh(buildRequest);
			}
		};
		init();

	}

	public LogPage(final IModel<Environment> environmentIModel, final IModel<String> componentName) {
		this.model = new LoadableDetachableModel<ProcessAdapter>() {
			@Override
			protected ProcessAdapter load() {
				return facade.refresh(new BuildRequest(Arrays.asList(componentName.getObject()),
						environmentIModel.getObject().getName()));
			}
		};
		init();
	}

	private void init() {
		if (model.getObject() == null) {
			getFeedbackPanel().error("Process not found");
		}
		add(new Label("info", new AbstractReadOnlyModel<Object>() {
			@Override
			public Object getObject() {
				ProcessAdapter object = model.getObject();
				BuildRequest request1 = object.getRequest();

				return request1.toString();
			}
		}));
		form = new Form("form");
		form.add(new LogPanel("log", model));
		add(form);
	}
}
