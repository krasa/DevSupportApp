package krasa.build.frontend.components;

import java.util.*;

import krasa.build.backend.dto.BuildJobDto;
import krasa.build.backend.facade.*;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.commons.*;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;

public class LastBuildsLeftPanel extends Panel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private BuildFacade facade;
	private IModel<List<BuildJobDto>> model;
	private WebMarkupContainer list;

	public LastBuildsLeftPanel(String id) {
		super(id);
		model = getLastFinishedBuildsModel();
		initList();
		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof ComponentChangedEvent) {
					ComponentChangedEvent changedEvent = (ComponentChangedEvent) message;
					if (changedEvent.getBuildableComponentDto().getStatus().isEnd()) {
						handler.add(list);
					}
				}
			}
		});
	}

	private IModel<List<BuildJobDto>> getLastFinishedBuildsModel() {
		return new LoadableDetachableModel<List<BuildJobDto>>() {

			@Override
			protected List<BuildJobDto> load() {
				return facade.getLastFinishedBuildJobs();
			}
		};
	}

	private void initList() {
		list = new WebMarkupContainer("list");
		ListView<BuildJobDto> runningBuildJobDtoListView = new ListView<BuildJobDto>("item", model) {

			@Override
			protected void populateItem(final ListItem<BuildJobDto> listItem) {

				BuildJobDto modelObject = listItem.getModelObject();
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", LogPage.class,
						LogPage.params(modelObject));

				PropertyModel<Object> status = new PropertyModel<>(listItem.getModel(), "status");
				link.add(new StyledLabel("status", status));
				link.add(new Label("name", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
				link.add(new Label("author", new PropertyModel<>(listItem.getModel(), "caller")));
				link.add(new Label("endTime", new DateModel<>(new PropertyModel<Date>(listItem.getModel(), "end"),
						"HH:mm")));

				listItem.add(link);
				listItem.setOutputMarkupId(true);

			}
		};
		list.setOutputMarkupId(true);
		list.add(runningBuildJobDtoListView);
		add(list);
	}

}
