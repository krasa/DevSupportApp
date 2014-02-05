package krasa.merge.frontend.component.merge;

import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.backend.facade.MergeEvent;
import krasa.merge.backend.service.MergeService;
import krasa.merge.frontend.pages.MergeLogPage;
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

import java.text.SimpleDateFormat;
import java.util.List;

public class CurrentlyMergingPanel extends Panel {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private MergeService mergeService;
	private ListView<MergeJobDto> runningMergeJobDtoListView;
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("mm:ss");
	private IModel<List<MergeJobDto>> currentlyMergeingModel;
	private WebMarkupContainer list;
	private AbstractAjaxTimerBehavior abstractAjaxTimerBehavior;

	@Subscribe
	public void refreshRow(AjaxRequestTarget target, MergeEvent message) {
		log.debug("Refreshing");
		target.add(list);
		if (abstractAjaxTimerBehavior.isStopped()) {
			abstractAjaxTimerBehavior.restart(target);
		}
	}

	public CurrentlyMergingPanel(String id) {
		super(id);
		currentlyMergeingModel = getCurrentlyMergeingModel();
		initList();
		addAjaxRefreshBehaviour();
	}

	private void addAjaxRefreshBehaviour() {
		abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				if (currentlyMergeingModel.getObject().size() != 0) {
					ajaxRequestTarget.add(list);
				} else {
					stop(ajaxRequestTarget);
				}
			}
		};
		add(abstractAjaxTimerBehavior);
	}

	private IModel<List<MergeJobDto>> getCurrentlyMergeingModel() {
		return new LoadableDetachableModel<List<MergeJobDto>>() {
			@Override
			protected List<MergeJobDto> load() {
				return mergeService.getRunningMergeJobs();
			}
		};
	}

	private void initList() {
		list = new WebMarkupContainer("list");
		runningMergeJobDtoListView = new ListView<MergeJobDto>("item", currentlyMergeingModel) {
			@Override
			protected void populateItem(final ListItem<MergeJobDto> listItem) {
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", MergeLogPage.class,
						MergeLogPage.params(listItem.getModelObject()));
				link.add(new Label("prefix", new RunningJobLabelModel(listItem)));
				link.add(new Label("component", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
				listItem.add(link);
				listItem.setOutputMarkupId(true);
			}
		};
		list.setOutputMarkupId(true);
		list.add(runningMergeJobDtoListView);
		add(list);
	}

	private static class RunningJobLabelModel extends AbstractReadOnlyModel<String> {
		private final ListItem<MergeJobDto> listItem;

		public RunningJobLabelModel(ListItem<MergeJobDto> listItem) {
			this.listItem = listItem;
		}

		@Override
		public String getObject() {
			MergeJobDto modelObject = listItem.getModelObject();

			String s = "";
			// if (modelObject.getStart() == null) {
			// s = "Pending, ";
			// }
			// String format = modelObject.getRemainsAsString();
			// if (format != null) {
			// s = format + ", ";
			// }

			return s;
		}

	}
}
