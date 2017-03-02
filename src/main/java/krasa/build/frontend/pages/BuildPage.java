package krasa.build.frontend.pages;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.components.BuildLeftPanel;
import krasa.build.frontend.components.BuildPageTopFormPanel;
import krasa.build.frontend.components.EnvironmentsListPanel;
import krasa.core.frontend.pages.BasePage;
import krasa.core.frontend.web.CookieUtils;

public class BuildPage extends BasePage {

	public static final String NAME = "env";
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static final String RESULT = "result";
	protected EnvironmentsListPanel environmets;
	@SpringBean
	private BuildFacade facade;
	protected IModel<Environment> model;

	public BuildPage() {
		String buildComponent = CookieUtils.getBuildComponent();
		if (StringUtils.isNotBlank(buildComponent)) {
			getRequestCycle().setResponsePage(BuildPage.class, createPageParameters(buildComponent));
		} else {
			// empty for performance
			queue(createEnvironmentPanel());
			queue(environmets = new EnvironmentsListPanel("environmets",
					new LoadableDetachableModel<List<Environment>>() {

						@Override
						protected List<Environment> load() {
							return Collections.emptyList();
						}
					}));
		}
	}

	public BuildPage(PageParameters parameters) {
		super(parameters);
		StringValue stringValue = parameters.get(NAME);
		String buildComponent = stringValue.toString();
		model = getEnvironmentModel(buildComponent);
		queue(createEnvironmentPanel());
		queue(environmets = new EnvironmentsListPanel("environmets", getEnvironmentsModel(model)));

		CookieUtils.setBuildComponent(buildComponent);
	}

	private BuildPageTopFormPanel createEnvironmentPanel() {
		return new BuildPageTopFormPanel("createEnvironment");
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new BuildLeftPanel(id);
	}

	private IModel<List<Environment>> getEnvironmentsModel() {
		return new LoadableDetachableModel<List<Environment>>() {

			@Override
			protected List<Environment> load() {
				return facade.getEnvironments();
			}
		};
	}

	private AbstractReadOnlyModel<List<Environment>> getEnvironmentsModel(final IModel<Environment> model1) {
		return new AbstractReadOnlyModel<List<Environment>>() {

			@Override
			public List<Environment> getObject() {
				Environment object = model1.getObject();
				if (object == null) {
					return Collections.emptyList();
				}
				return Collections.singletonList(object);
			}
		};
	}

	private LoadableDetachableModel<Environment> getEnvironmentModel(final String s) {
		return new LoadableDetachableModel<Environment>() {

			@Override
			protected Environment load() {
				return facade.getEnvironmentByName(s);
			}
		};
	}

	public static PageParameters createPageParameters(String buildComponent) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(NAME, buildComponent);
		return pageParameters;
	}
}
