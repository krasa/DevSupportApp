package krasa.merge.frontend.pages;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.backend.service.MergeService;

import org.apache.wicket.Component;
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

/**
 * @author Vojtech Krasa
 */
public class MergeLogPage extends BasePage {

	public static final String COMPONENT_ID = "componentId";
	public static final String ID = "buildJobId";
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected Form form;
	private IModel<MergeJobDto> model;
	@SpringBean
	protected MergeService facade;
	private Integer buildJobId;

	public MergeLogPage(PageParameters parameters) {
		super(parameters);
		initializeJobId();

		this.model = new LoadableDetachableModel<MergeJobDto>() {
			@Override
			protected MergeJobDto load() {
				return facade.getMergeJobById(buildJobId);
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

	private void initializeJobId() {
		StringValue stringValue = getPageParameters().get(ID);
		buildJobId = stringValue.toInt();
	}

	private void init() {
		if (model.getObject() == null) {
			getFeedbackPanel().error("Process not found");
		}
		add(new Label("info", new AbstractReadOnlyModel<Object>() {
			@Override
			public Object getObject() {
				MergeJobDto object = model.getObject();
				return object.toString();
			}
		}));
		form = new Form("form");
		// form.add(new LogPanel("log", model));
		add(form);
	}

	// @Override
	// protected Component newLeftColumnPanel(String id) {
	// return new MergeLeftPanel(id, null);
	// }

	public static PageParameters params(MergeJobDto buildJob) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(ID, buildJob.getMergeJobId());
		return pageParameters;
	}
}
