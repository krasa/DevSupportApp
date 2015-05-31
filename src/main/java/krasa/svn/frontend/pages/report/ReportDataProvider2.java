package krasa.svn.frontend.pages.report;

import java.util.*;

import krasa.svn.backend.dto.ReportItem;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.model.*;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.google.common.collect.*;

public class ReportDataProvider2 implements ISortableDataProvider<ReportItem, String> {
	List<ReportItem> uniqueCPRs;

	public ReportDataProvider2(IModel<List<SVNLogEntry>> merges) {
		uniqueCPRs = new ArrayList<>();
		Multimap<String, SVNLogEntry> stringSVNLogEntryMultimap = filterAndSort(merges.getObject());
		for (String entry : stringSVNLogEntryMultimap.keySet()) {
			uniqueCPRs.add(new ReportItem(entry, stringSVNLogEntryMultimap.get(entry)));
		}
	}

	private Multimap<String, SVNLogEntry> filterAndSort(List<SVNLogEntry> revisionsByBranchName) {
		TreeMultimap<String, SVNLogEntry> multiHashMap = TreeMultimap.create(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}, new Comparator<SVNLogEntry>() {
			@Override
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

	@Override
	public void detach() {
	}

	@Override
	public Iterator<? extends ReportItem> iterator(long first, long count) {
		return uniqueCPRs.iterator();
	}

	@Override
	public long size() {
		return uniqueCPRs.size();
	}

	@Override
	public IModel<ReportItem> model(ReportItem object) {
		return new Model<>(object);
	}

	@Override
	public ISortState getSortState() {
		return new SingleSortState();
	}
}
