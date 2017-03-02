package krasa.svn.frontend.component.merge;

import java.util.*;

import krasa.automerge.MergeEvent;
import krasa.core.frontend.commons.*;
import krasa.core.frontend.components.BasePanel;
import krasa.core.frontend.pages.FileSystemLogPage;
import krasa.svn.backend.dto.MergeJobDto;
import krasa.svn.backend.service.MergeFacade;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;

public class MergesPanel extends BasePanel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private MergeFacade facade;
	private IModel<List<MergeJobDto>> model;

	public MergesPanel(String id) {
		super(id);
		model = getLastFinishedMergesModel();
		initList();
		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof MergeEvent) {
					handler.add(MergesPanel.this);
				}
			}
		});
	}

	private IModel<List<MergeJobDto>> getLastFinishedMergesModel() {
		return new LoadableDetachableModel<List<MergeJobDto>>() {

			@Override
			protected List<MergeJobDto> load() {
				return facade.getLastMergeJobs();
			}
		};
	}

	private void initList() {
		WebMarkupContainer list = new WebMarkupContainer("list");
		ListView<MergeJobDto> runningMergeJobDtoListView = new ListView<MergeJobDto>("item", model) {

			@Override
			protected void populateItem(ListItem<MergeJobDto> listItem) {
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", FileSystemLogPage.class,
						FileSystemLogPage.params(listItem.getModelObject()));
				PropertyModel<Object> status = new PropertyModel<>(listItem.getModel(), "status");
				link.add(new StyledLabel("status", status));
				link.add(new StyledLabel("mode", new PropertyModel<>(listItem.getModel(), "autoMergeJobMode")));
				link.add(new Label("to", new PropertyModel<>(listItem.getModel(), "to")));
				link.add(new Label("from", new PropertyModel<>(listItem.getModel(), "from")));
				link.add(new Label("startTime", new DateModel<>(new PropertyModel<Date>(listItem.getModel(),
						"startTime"), "HH:mm")));
				link.add(new Label("endTime", new DateModel<>(new PropertyModel<Date>(listItem.getModel(), "endTime"),
						"HH:mm dd.MM.")));
				link.add(new Label("caller", new PropertyModel<>(listItem.getModel(), "caller")));

				listItem.add(link);
				listItem.setOutputMarkupId(true);

			}
		};
		list.setOutputMarkupId(true);
		list.add(runningMergeJobDtoListView);
		add(list);
	}

}
