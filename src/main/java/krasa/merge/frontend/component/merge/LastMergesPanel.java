package krasa.merge.frontend.component.merge;

import krasa.build.backend.facade.ComponentBuildEvent;
import krasa.core.frontend.commons.DateModel;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.backend.service.MergeService;
import krasa.merge.frontend.pages.MergeLogPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public class LastMergesPanel extends Panel {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	private MergeService facade;
	private ListView<MergeJobDto> runningMergeJobDtoListView;
	private IModel<List<MergeJobDto>> model;
	private WebMarkupContainer list;

	@Subscribe
	public void refreshRow(AjaxRequestTarget target, ComponentBuildEvent message) {
		WebRequest req = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpReq = (HttpServletRequest) req.getContainerRequest();
		String clientAddress = httpReq.getRemoteHost();
		log.debug("Refreshing ip:{}", clientAddress);
		target.add(list);
	}

	public LastMergesPanel(String id) {
		super(id);
		model = getLastFinishedBuildsModel();
		initList();
	}

	private IModel<List<MergeJobDto>> getLastFinishedBuildsModel() {
		return new LoadableDetachableModel<List<MergeJobDto>>() {

			@Override
			protected List<MergeJobDto> load() {
				return facade.getLastFinishedJobs();
			}
		};
	}

	private void initList() {
		list = new WebMarkupContainer("list");
		runningMergeJobDtoListView = new ListView<MergeJobDto>("item", model) {

			@Override
			protected void populateItem(final ListItem<MergeJobDto> listItem) {

				MergeJobDto modelObject = listItem.getModelObject();
				LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink("link", MergeLogPage.class,
						MergeLogPage.params(modelObject));

				PropertyModel<Object> status = new PropertyModel<>(listItem.getModel(), "status");
				Label label = new Label("status", status);
				label.add(new AttributeModifier("id", status));
				link.add(label);
				link.add(new Label("name", new PropertyModel<>(listItem.getModel(), "component")));
				link.add(new Label("environment", new PropertyModel<>(listItem.getModel(), "environment")));
				link.add(new Label("endTime", new DateModel<>(new PropertyModel<Date>(listItem.getModel(), "end"),
						"HH:mm")));

				listItem.add(link);
				listItem.setOutputMarkupId(true);

			}
		};
		list.setOutputMarkupId(true);
		list.add(runningMergeJobDtoListView);
		add(list);
	}

}
