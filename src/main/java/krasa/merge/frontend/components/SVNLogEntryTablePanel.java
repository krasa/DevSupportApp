package krasa.merge.frontend.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import krasa.core.frontend.components.DateModel;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogEntryTablePanel extends Panel {
	public SVNLogEntryTablePanel(String id, final IModel<List<SVNLogEntry>> model) {
		super(id, model);
		final ArrayList<IColumn<SVNLogEntry, String>> columns = getColumns();
		AjaxFallbackDefaultDataTable<SVNLogEntry, String> table = new AjaxFallbackDefaultDataTable<SVNLogEntry, String>(
				"merges", columns, new DataProvider(model), 100);
		add(table);
	}

	private ArrayList<IColumn<SVNLogEntry, String>> getColumns() {
		final ArrayList<IColumn<SVNLogEntry, String>> columns = new ArrayList<IColumn<SVNLogEntry, String>>();
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<String>("revision"), "revision") {
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				long revision = rowModel.getObject().getRevision();
				Fragment link = new Fragment(componentId, "linkFragment", SVNLogEntryTablePanel.this);
				link.add(new FishEyeLink("link", new FishEyeLinkModel(revision), Model.of(revision)));
				cellItem.add(link);
			}
		});
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<String>("message"), "message") {
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
			}
		});
		columns.add(new PropertyColumn<SVNLogEntry, String>(new Model<String>("author"), "author", "author"));
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<String>("date"), "date") {
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				PropertyModel<Date> date = new PropertyModel<Date>(rowModel, "date");
				cellItem.add(new Label(componentId, new DateModel(date)));
			}
		});
		return columns;
	}

	private AbstractReadOnlyModel<List<? extends MergeInfoResultItem>> getAbstractReadOnlyModel(
			final IModel<MergeInfoResult> model) {
		return new AbstractReadOnlyModel<List<? extends MergeInfoResultItem>>() {
			@Override
			public List<? extends MergeInfoResultItem> getObject() {
				return model.getObject().getMergeInfoResultItems();
			}
		};
	}

	private class DataProvider implements ISortableDataProvider<SVNLogEntry, String> {
		IModel<List<SVNLogEntry>> model;

		public DataProvider(IModel<List<SVNLogEntry>> model) {
			this.model = model;
		}

		public void detach() {
		}

		public Iterator<? extends SVNLogEntry> iterator(long first, long count) {
			return model.getObject().iterator();
		}

		public long size() {
			return model.getObject().size();
		}

		public IModel<SVNLogEntry> model(SVNLogEntry object) {
			return new Model<SVNLogEntry>(object);
		}

		public ISortState getSortState() {
			return new SingleSortState();
		}
	}
}
