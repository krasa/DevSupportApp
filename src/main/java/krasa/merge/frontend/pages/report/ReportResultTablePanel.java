package krasa.merge.frontend.pages.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.frontend.components.FishEyeLink;
import krasa.merge.frontend.components.FishEyeLinkModel;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class ReportResultTablePanel extends Panel {
	public ReportResultTablePanel(String id, final IModel<List<SVNLogEntry>> model) {
		super(id, model);
		final ArrayList<IColumn<ReportItem, String>> columns = getColumns();
		AjaxFallbackDefaultDataTable<ReportItem, String> table = new AjaxFallbackDefaultDataTable<ReportItem, String>(
				"merges", columns, new DataProvider(model), 100);
		add(table);
	}

	private ArrayList<IColumn<ReportItem, String>> getColumns() {
		final ArrayList<IColumn<ReportItem, String>> columns = new ArrayList<IColumn<ReportItem, String>>();
		columns.add(new AbstractColumn<ReportItem, String>(new Model<String>("revision"), "revision") {
			public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
					IModel<ReportItem> rowModel) {
				ReportItem object = rowModel.getObject();
				RepeatingView links = new RepeatingView(componentId);
				for (SVNLogEntry commit : object.commits) {
					Fragment link = new Fragment(links.newChildId(), "linkFragment", ReportResultTablePanel.this);
					link.add(new FishEyeLink("link", new FishEyeLinkModel(commit.getRevision()),
							Model.of(commit.getRevision() + " " + getSubTaskAndMessage(commit)), new Model<String>(
									commit.toString())));
					links.add(link);
				}
				cellItem.add(links);

			}

			private String getSubTaskAndMessage(SVNLogEntry commit) {
				String message = commit.getMessage();
				int beginIndex = ReportResult.nthOccurrence(message, "=>", 1);
				if (beginIndex < 0) {
					beginIndex = message.length();
				} else {
					beginIndex = beginIndex + 3;
				}
				return message.substring(beginIndex);
			}
		});
		columns.add(new AbstractColumn<ReportItem, String>(new Model<String>("message"), "message") {
			public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
					IModel<ReportItem> rowModel) {
				cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
			}
		});
		// columns.add(new PropertyColumn<ReportItem, String>(new Model<String>("author"), "author", "author"));
		// columns.add(new AbstractColumn<ReportItem, String>(new Model<String>("date"), "date") {
		// public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
		// IModel<ReportItem> rowModel) {
		// PropertyModel<Date> date = new PropertyModel<Date>(rowModel, "date");
		// cellItem.add(new Label(componentId, new DateModel(date)));
		// }
		// });
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

	private class DataProvider implements ISortableDataProvider<ReportItem, String> {
		List<ReportItem> uniqueCPRs;

		public DataProvider(IModel<List<SVNLogEntry>> merges) {
			uniqueCPRs = new ArrayList<ReportItem>();
			Multimap<String, SVNLogEntry> stringSVNLogEntryMultimap = filterAndSort(merges.getObject());
			for (String entry : stringSVNLogEntryMultimap.keySet()) {
				uniqueCPRs.add(new ReportItem(entry, stringSVNLogEntryMultimap.get(entry)));
			}
		}

		public void detach() {
		}

		public Iterator<? extends ReportItem> iterator(long first, long count) {
			return uniqueCPRs.iterator();
		}

		public long size() {
			return uniqueCPRs.size();
		}

		public IModel<ReportItem> model(ReportItem object) {
			return new Model<ReportItem>(object);
		}

		public ISortState getSortState() {
			return new SingleSortState();
		}
	}

	class ReportItem implements Serializable {
		public String message;
		public Collection<SVNLogEntry> commits;

		ReportItem(String message, Collection<SVNLogEntry> commits) {
			this.message = message;
			this.commits = commits;
		}
	}

	private Multimap<String, SVNLogEntry> filterAndSort(List<SVNLogEntry> revisionsByBranchName) {
		TreeMultimap<String, SVNLogEntry> multiHashMap = TreeMultimap.create(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}, new Comparator<SVNLogEntry>() {
			public int compare(SVNLogEntry o1, SVNLogEntry o2) {
				return (int) (o1.getRevision() - o2.getRevision());
			}
		});
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			String message = svnLogEntry.getMessage();
			if (message.contains("=>")) {
				int i = message.indexOf("=>");
				multiHashMap.put(message.substring(0, i), svnLogEntry);
			} else if (message.startsWith("##admin Creating a new") || message.startsWith("##merge")) {
				// do not add
			} else {
				multiHashMap.put(message, svnLogEntry);
			}
		}
		return multiHashMap;
	}

}
