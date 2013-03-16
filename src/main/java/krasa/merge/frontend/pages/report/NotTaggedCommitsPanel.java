package krasa.merge.frontend.pages.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.frontend.component.table.SVNLogEntryTablePanel;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;

public class NotTaggedCommitsPanel extends Panel {
	protected AjaxFallbackDefaultDataTable<SvnFolder, String> table;

	public NotTaggedCommitsPanel(String id, final IModel<ReportResult> model) {
		super(id, model);
		final ReportResult object = model.getObject();
		final Map<String, List<SVNLogEntry>> svnFolderListMap = object.getSvnFolderListMap();
		List<String> branches = new ArrayList<String>(svnFolderListMap.keySet());
		Collections.sort(branches);

		ListView<String> components = new ListView<String>("resultItem", branches) {
			@Override
			protected void populateItem(ListItem<String> components) {
				final String branchName = components.getModelObject();
				RepeatingView view = new RepeatingView("repeater");

				// incremental mezi tagy
				List<SVNDirEntry> tagsByBranchaName = model.getObject().getTagsByBranchaName(
						components.getModelObject());
				if (tagsByBranchaName.isEmpty()) {
					// next tag is null
					Fragment tagFragment = new Fragment(view.newChildId(), "tableFragment", NotTaggedCommitsPanel.this);
					tagFragment.add(new Label("title", "Branch " + branchName + ", no tag created yet"));
					tagFragment.add(new SVNLogEntryTablePanel("table", getModel(branchName)));

					view.add(tagFragment);
				}
				for (int i = 0; i < tagsByBranchaName.size(); i++) {
					final SVNDirEntry tag = tagsByBranchaName.get(i);
					SVNDirEntry nextTag = null;
					if (tagsByBranchaName.size() > i + 1) {
						nextTag = tagsByBranchaName.get(i + 1);
					}
					if (nextTag == null) {
						// next tag is null
						Fragment tagFragment = new Fragment(view.newChildId(), "tableFragment",
								NotTaggedCommitsPanel.this);
						tagFragment.add(new Label("title", getDifferenceTitle(branchName, tag, model)));
						tagFragment.add(new SVNLogEntryTablePanel("table", getModel(branchName, tag, model)));
						view.add(tagFragment);
					}

				}

				components.add(view);
			}

			private AbstractReadOnlyModel<List<SVNLogEntry>> getModel(final String branchName) {
				return new AbstractReadOnlyModel<List<SVNLogEntry>>() {
					@Override
					public List<SVNLogEntry> getObject() {
						return model.getObject().getAllCommits(branchName);
					}
				};
			}

			private AbstractReadOnlyModel<List<SVNLogEntry>> getModel(final String branchName, final SVNDirEntry tag,
					final IModel<ReportResult> model) {
				return new AbstractReadOnlyModel<List<SVNLogEntry>>() {
					@Override
					public List<SVNLogEntry> getObject() {
						return model.getObject().getDifferenceBetweenTagAndBranchAsStrings(tag, branchName);
					}
				};
			}

		};
		add(components);
	}

	private String getDifferenceTitle(String branchName, SVNDirEntry tag, IModel<ReportResult> model) {
		long lastRevisionOfBranch = model.getObject().getLastRevisionOfBranch(branchName);
		return "Branch " + branchName + ", difference between " + tag.getName() + " and " + branchName
				+ ", revisions [" + tag.getRevision() + "-" + lastRevisionOfBranch + "]";
	}

}
