package krasa.build.frontend.components;

import java.util.List;

import krasa.build.backend.dto.BuildJobDto;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;

import org.apache.wicket.ajax.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.*;

public class CurrentlyBuildingLeftPanel extends Panel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private BuildFacade facade;
	private IModel<List<BuildJobDto>> currentlyBuildingModel;
	private WebMarkupContainer list;

	public CurrentlyBuildingLeftPanel(String id) {
		super(id);
		currentlyBuildingModel = getCurrentlyBuildingModel();
		initList();
		addAjaxRefreshBehaviour();
	}

	private void addAjaxRefreshBehaviour() {
		AbstractAjaxTimerBehavior abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				ajaxRequestTarget.add(list);
			}
		};
		add(abstractAjaxTimerBehavior);
	}

	private IModel<List<BuildJobDto>> getCurrentlyBuildingModel() {
		return new LoadableDetachableModel<List<BuildJobDto>>() {

			@Override
			protected List<BuildJobDto> load() {
				return facade.getRunningBuildJobs();
			}
		};
	}

	private void initList() {
		list = new WebMarkupContainer("list");
		ListView<BuildJobDto> runningBuildJobDtoListView = new ListView<BuildJobDto>("item", currentlyBuildingModel) {

			@Override
			protected void populateItem(final ListItem<BuildJobDto> listItem) {
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", LogPage.class,
						LogPage.params(listItem.getModelObject()));
				link.add(new Label("prefix", new RunningJobLabelModel(listItem)));
				link.add(new Label("component", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
				link.add(new Label("author", new PropertyModel<>(listItem.getModel(), "caller")));
				listItem.add(link);
				listItem.setOutputMarkupId(true);
			}
		};
		list.setOutputMarkupId(true);
		list.add(runningBuildJobDtoListView);
		add(list);
	}

	private static class RunningJobLabelModel extends AbstractReadOnlyModel<String> {

		private final ListItem<BuildJobDto> listItem;

		public RunningJobLabelModel(ListItem<BuildJobDto> listItem) {
			this.listItem = listItem;
		}

		@Override
		public String getObject() {
			BuildJobDto modelObject = listItem.getModelObject();

			String s = "";
			if (modelObject.getStart() == null) {
				s = "Pending, ";
			}
			String format = modelObject.getRemainsAsString();
			if (format != null) {
				s = format + ", ";
			}

			return s;
		}

	}
}
