package krasa.merge.frontend.pages.mergeinfo;

import java.util.List;

import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.frontend.component.table.SVNLogEntryTablePanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResultPanel extends Panel {

	public MergeInfoResultPanel(String id, final LoadableDetachableModel<MergeInfoResult> loadableDetachableModel) {
		super(id);
		setOutputMarkupId(true);
		add(createResultList(loadableDetachableModel));
	}

	private ListView<MergeInfoResultItem> createResultList(
			final LoadableDetachableModel<MergeInfoResult> loadableDetachableModel) {
		return new ListView<MergeInfoResultItem>("resultItem",
				new AbstractReadOnlyModel<List<? extends MergeInfoResultItem>>() {
					@Override
					public List<? extends MergeInfoResultItem> getObject() {
						return loadableDetachableModel.getObject().getMergeInfoResultItems();
					}
				}) {
			@Override
			protected void populateItem(final ListItem<MergeInfoResultItem> components) {
				components.add(new Label("from", new PropertyModel<String>(components.getModel(), "from")));
				components.add(new Label("to", new PropertyModel<String>(components.getModel(), "to")));
				components.add(new SVNLogEntryTablePanel("tablePanel", new AbstractReadOnlyModel<List<SVNLogEntry>>() {
					@Override
					public List<SVNLogEntry> getObject() {
						return components.getModel().getObject().getMerges();
					}
				}));
			}
		};
	}
}
