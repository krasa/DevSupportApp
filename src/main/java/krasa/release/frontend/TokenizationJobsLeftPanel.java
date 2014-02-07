package krasa.release.frontend;

import java.util.Date;
import java.util.List;

import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.release.domain.TokenizationJob;
import krasa.release.service.TokenizationService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

public class TokenizationJobsLeftPanel extends Panel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private TokenizationService facade;
	private ListView<TokenizationJob> jobsList;
	private IModel<List<TokenizationJob>> jobsModel;
	private WebMarkupContainer listContainer;
	private AbstractAjaxTimerBehavior abstractAjaxTimerBehavior;

	public TokenizationJobsLeftPanel(String id) {
		super(id);
		jobsModel = getJobsModel();
		initList();
		addAjaxRefreshBehaviour();
	}

	private void addAjaxRefreshBehaviour() {
		abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				ajaxRequestTarget.add(listContainer);
			}
		};
		add(abstractAjaxTimerBehavior);
	}

	private IModel<List<TokenizationJob>> getJobsModel() {
		return new LoadableDetachableModel<List<TokenizationJob>>() {

			@Override
			protected List<TokenizationJob> load() {
				return facade.getJobs();
			}
		};
	}

	private void initList() {
		listContainer = new WebMarkupContainer("list");
		jobsList = new ListView<TokenizationJob>("item", jobsModel) {

			@Override
			protected void populateItem(final ListItem<TokenizationJob> listItem) {
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", FileSystemLogPage.class,
						FileSystemLogPage.params(listItem.getModelObject()));
				link.add(new Label("status", new RunningJobLabelModel(listItem)));
				link.add(new Label("branchNamePattern", new PropertyModel<>(listItem.getModel(), "branchNamePattern")));
				link.add(new Label("fromVersion", new PropertyModel<>(listItem.getModel(), "fromVersion")));
				link.add(new Label("toVersion", new PropertyModel<>(listItem.getModel(), "toVersion")));
				listItem.add(link);
				listItem.setOutputMarkupId(true);
			}
		};
		listContainer.setOutputMarkupId(true);
		listContainer.add(jobsList);
		add(listContainer);
	}

	private static class RunningJobLabelModel extends AbstractReadOnlyModel<String> {

		private final ListItem<TokenizationJob> listItem;

		public RunningJobLabelModel(ListItem<TokenizationJob> listItem) {
			this.listItem = listItem;
		}

		@Override
		public String getObject() {
			TokenizationJob modelObject = listItem.getModelObject();

			String s = "";
			Date start = modelObject.getStart();
			Date end = modelObject.getEnd();
			if (end != null) {
				s = modelObject.getStatus().name() + "; end=" + toString(end);
			} else if (start != null) {
				s = modelObject.getStatus().name() + "; start=" + toString(start);
			} else if (start == null) {
				s = modelObject.getStatus().name();
			}

			return s;
		}

		protected String toString(Date start) {
			if (start == null) {
				return null;
			}
			return DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(start);
		}

	}
}
