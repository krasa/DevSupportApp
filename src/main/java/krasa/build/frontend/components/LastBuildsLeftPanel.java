package krasa.build.frontend.components;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.build.backend.dto.BuildJobDto;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.backend.facade.ComponentChangedEvent;
import krasa.build.backend.facade.UsernameException;
import krasa.build.frontend.pages.BuildLogPage;
import krasa.core.frontend.StaticImage;
import krasa.core.frontend.commons.ButtonPanel;
import krasa.core.frontend.commons.DateModel;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.core.frontend.commons.StyledLabel;
import krasa.core.frontend.pages.BasePage;

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
			protected void populateItem(ListItem<BuildJobDto> listItem) {

				BuildJobDto modelObject = listItem.getModelObject();
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", BuildLogPage.class,
						BuildLogPage.params(modelObject));

				PropertyModel<Object> status = new PropertyModel<>(listItem.getModel(), "status");
				link.add(new StyledLabel("status", status));
				link.add(new Label("name", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
				link.add(new Label("author", new PropertyModel<>(listItem.getModel(), "caller")));
				link.add(new Label("endTime", new DateModel<>(new PropertyModel<Date>(listItem.getModel(), "end"),
						"HH:mm")));

				listItem.add(link);
				Form<BuildJobDto> form = new Form<>("form", listItem.getModel());
				form.add(new ButtonPanel("rerun", "Rerun", StaticImage.RERUN2) {

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						BuildJobDto modelObject1 = (BuildJobDto) form.getModelObject();
						BuildableComponentDto transform = BuildableComponentDto.byId(modelObject1.getComponentId());
						try {
							facade.buildComponent(transform);
						} catch (ProcessAlreadyRunning e) {
						} catch (UsernameException e) {
							error(e.getMessage());
							target.add(((BasePage) this.getPage()).getFeedbackPanel());
							target.appendJavaScript("alert('" + e.getMessage() + "');");
						}
					}
				});
				listItem.add(form);
				listItem.setOutputMarkupId(true);

			}
		};
		list.setOutputMarkupId(true);
		list.add(runningBuildJobDtoListView);
		add(list);
	}

}
