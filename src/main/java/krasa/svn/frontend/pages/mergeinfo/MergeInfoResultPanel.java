package krasa.svn.frontend.pages.mergeinfo;

import krasa.svn.backend.dto.MergeInfoResult;
import krasa.svn.backend.dto.MergeInfoResultItem;
import krasa.svn.frontend.component.table.SVNLogEntryTablePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResultPanel extends Panel {

	public MergeInfoResultPanel(String id, LoadableDetachableModel<MergeInfoResult> loadableDetachableModel) {
		super(id);
		setOutputMarkupId(true);
		add(createResultList(loadableDetachableModel));
	}

	private ListView<MergeInfoResultItem> createResultList(
			final LoadableDetachableModel<MergeInfoResult> loadableDetachableModel) {
		AbstractReadOnlyModel<List<MergeInfoResultItem>> model = new AbstractReadOnlyModel<List<MergeInfoResultItem>>() {

			@Override
			public List<MergeInfoResultItem> getObject() {
				return loadableDetachableModel.getObject().getMergeInfoResultItems();
			}
		};
		return new ListView<MergeInfoResultItem>("resultItem", model) {

			@Override
			protected void populateItem(final ListItem<MergeInfoResultItem> components) {
				components.add(new Label("from", new PropertyModel<String>(components.getModel(), "from")));
				components.add(new Label("to", new PropertyModel<String>(components.getModel(), "to")));
				components.add(new SVNLogEntryTablePanel("tablePanel",
						new AbstractReadOnlyModel<MergeInfoResultItem>() {

							@Override
							public MergeInfoResultItem getObject() {
								MergeInfoResultItem object = components.getModel().getObject();
								return object;
							}
						}));
			}
		};
	}
}
