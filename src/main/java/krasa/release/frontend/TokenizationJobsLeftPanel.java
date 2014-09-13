package krasa.release.frontend;

import java.util.*;

import krasa.core.frontend.commons.*;
import krasa.core.frontend.components.BasePanel;
import krasa.core.frontend.pages.FileSystemLogPage;
import krasa.release.domain.TokenizationJob;
import krasa.release.service.*;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;

public class TokenizationJobsLeftPanel extends BasePanel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private TokenizationService facade;
	private ListView<TokenizationJob> jobsList;
	private IModel<List<TokenizationJob>> jobsModel;
	private WebMarkupContainer listContainer;

	public TokenizationJobsLeftPanel(String id) {
		super(id);
		jobsModel = getJobsModel();
		initList();
		add(new WebSocketBehavior() {
			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof TokenizationEvent) {
					handler.add(TokenizationJobsLeftPanel.this);
				}
			}
		});
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
				link.add(new StyledLabel("status", new PropertyModel(listItem.getModel(), "status")));
				link.add(new Label("text", new RunningJobLabelModel(listItem)));
				link.add(new Label("caller", new PropertyModel(listItem.getModel(), "caller")));
				link.add(new Label("branchNamePattern", new PropertyModel<>(listItem.getModel(), "branchNamePattern")));
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
				s = "end=" + toString(end);
			} else if (start != null) {
				s = "start=" + toString(start);
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
