package krasa.merge.frontend.pages.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.ReportResult;

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

/**
 * @author Vojtech Krasa
 */
public class ReportResultPanel extends Panel {
	public static final int ROWS_PER_PAGE = 100000;
	protected AjaxFallbackDefaultDataTable<SvnFolder, String> table;
	private IModel<ReportResult> model;

	public ReportResultPanel(String id) {
		super(id);
	}

	public ReportResultPanel(String id, final IModel<ReportResult> model) {
		super(id, model);
		this.model = model;
		final ReportResult object = model.getObject();
		final Map<String, List<SVNLogEntry>> svnFolderListMap = object.getSvnFolderListMap();
		List<String> branches = new ArrayList<String>(svnFolderListMap.keySet());
		Collections.sort(branches);

		ListView<String> components = new ListView<String>("resultItem", branches) {
			@Override
			protected void populateItem(ListItem<String> components) {
				final String branchName = components.getModelObject();
				RepeatingView view = new RepeatingView("repeater");
				Fragment tableFragment = new Fragment(view.newChildId(), "tableFragment", ReportResultPanel.this);

				tableFragment.add(new Label("title", "Branch " + branchName + ", revisions ["
						+ model.getObject().getFirstRevisionOfBranch(branchName) + "-"
						+ model.getObject().getLastRevisionOfBranch(branchName) + "]"));

				tableFragment.add(new ReportResultTablePanel("table", new AbstractReadOnlyModel<List<SVNLogEntry>>() {
					@Override
					public List<SVNLogEntry> getObject() {
						return model.getObject().getIssues(branchName);
					}
				}));

				view.add(tableFragment);

				// incremental mezi tagy
				List<SVNDirEntry> tagsByBranchaName = model.getObject().getTagsByBranchaName(
						components.getModelObject());
				for (int i = 0; i < tagsByBranchaName.size(); i++) {
					final SVNDirEntry tag = tagsByBranchaName.get(i);
					SVNDirEntry nextTag = null;
					if (tagsByBranchaName.size() > i + 1) {
						nextTag = tagsByBranchaName.get(i + 1);
					}
					Fragment tagFragment = new Fragment(view.newChildId(), "tableFragment", ReportResultPanel.this);
					tagFragment.add(new Label("title", getTagIssuesTitle(branchName, tag, model)));
					tagFragment.add(new ReportResultTablePanel("table", new AbstractReadOnlyModel<List<SVNLogEntry>>() {
						@Override
						public List<SVNLogEntry> getObject() {
							return model.getObject().getIssuesByTag(branchName, tag);
						}
					}));
					view.add(tagFragment);
					if (nextTag != null) {
						tagFragment = new Fragment(view.newChildId(), "tableFragment", ReportResultPanel.this);
						tagFragment.add(new Label("title", getTitle(branchName, tag, nextTag)));
						tagFragment.add(new ReportResultTablePanel("table",
								new AbstractReadOnlyModel<List<SVNLogEntry>>() {
									@Override
									public List<SVNLogEntry> getObject() {
										return model.getObject().getIncrementalIssuesByTag(branchName, tag);
									}
								}));
						view.add(tagFragment);
					} else {
						// next tag is null
						tagFragment = new Fragment(view.newChildId(), "tableFragment", ReportResultPanel.this);
						tagFragment.add(new Label("title", getDifferenceTitle(branchName, tag, model)));
						tagFragment.add(new ReportResultTablePanel("table",
								new AbstractReadOnlyModel<List<SVNLogEntry>>() {
									@Override
									public List<SVNLogEntry> getObject() {
										return model.getObject().getDifferenceBetweenTagAndBranchAsStrings(tag,
												branchName);
									}
								}));

						view.add(tagFragment);
					}

				}

				components.add(view);
			}

		};
		add(components);
	}

	private String getTagIssuesTitle(String branchName, SVNDirEntry tag, IModel<ReportResult> model) {
		long firstRevisionOfBranch = model.getObject().getFirstRevisionOfBranch(branchName);
		return "Branch " + branchName + ", tag " + tag.getName() + ", revisions [" + firstRevisionOfBranch + "-"
				+ tag.getRevision() + "]";

	}

	private String getTitle(String branchName, SVNDirEntry tag, SVNDirEntry nextTag) {
		long nextTagRevision = nextTag.getRevision();
		String nextTagName = nextTag.getName();
		return "Branch " + branchName + ", difference between " + tag.getName() + " and " + nextTagName
				+ ", revisions [" + tag.getRevision() + "-" + nextTagRevision + "]";
	}

	private String getDifferenceTitle(String branchName, SVNDirEntry tag, IModel<ReportResult> model) {
		long lastRevisionOfBranch = model.getObject().getLastRevisionOfBranch(branchName);
		return "Branch " + branchName + ", difference between " + tag.getName() + " and " + branchName
				+ ", revisions [" + tag.getRevision() + "-" + lastRevisionOfBranch + "]";
	}

}
