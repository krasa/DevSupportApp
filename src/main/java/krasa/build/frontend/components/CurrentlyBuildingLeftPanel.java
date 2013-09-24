package krasa.build.frontend.components;

import java.text.SimpleDateFormat;
import java.util.List;

import krasa.build.backend.dto.BuildJobDto;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.backend.facade.ComponentBuildEvent;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentlyBuildingLeftPanel extends Panel {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private BuildFacade facade;
	private ListView<BuildJobDto> runningBuildJobDtoListView;
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("mm:ss");
	private IModel<List<BuildJobDto>> currentlyBuildingModel;
	private WebMarkupContainer list;
	private AbstractAjaxTimerBehavior abstractAjaxTimerBehavior;

	@Subscribe
	public void refreshRow(AjaxRequestTarget target, ComponentBuildEvent message) {
		log.debug("Refreshing");
		target.add(list);
		if (abstractAjaxTimerBehavior.isStopped()) {
			abstractAjaxTimerBehavior.restart(target);
		}
	}

	public CurrentlyBuildingLeftPanel(String id) {
		super(id);
		currentlyBuildingModel = getCurrentlyBuildingModel();
		initList();
		addAjaxRefreshBehaviour();
	}

	private void addAjaxRefreshBehaviour() {
		abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				if (currentlyBuildingModel.getObject().size() != 0) {
					ajaxRequestTarget.add(list);
				} else {
					stop(ajaxRequestTarget);
				}
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
		runningBuildJobDtoListView = new ListView<BuildJobDto>("item", currentlyBuildingModel) {
			@Override
			protected void populateItem(final ListItem<BuildJobDto> listItem) {
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", LogPage.class,
						LogPage.params(listItem.getModelObject()));
				link.add(new Label("prefix", new RunningJobLabelModel(listItem)));
				link.add(new Label("component", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
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
