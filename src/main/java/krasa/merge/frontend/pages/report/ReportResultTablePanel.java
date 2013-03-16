package krasa.merge.frontend.pages.report;

import java.util.ArrayList;
import java.util.List;

import krasa.core.frontend.commons.FishEyeLink;
import krasa.core.frontend.commons.FishEyeLinkModel;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.dto.ReportItem;
import krasa.merge.backend.dto.ReportResult;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
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

public class ReportResultTablePanel extends Panel {
	public ReportResultTablePanel(String id, final IModel<List<SVNLogEntry>> model) {
		super(id, model);
		final ArrayList<IColumn<ReportItem, String>> columns = getColumns();
		AjaxFallbackDefaultDataTable<ReportItem, String> table = new AjaxFallbackDefaultDataTable<ReportItem, String>(
				"merges", columns, new ReportDataProvider(model), 100);
		add(table);
	}

	private ArrayList<IColumn<ReportItem, String>> getColumns() {
		final ArrayList<IColumn<ReportItem, String>> columns = new ArrayList<IColumn<ReportItem, String>>();
		columns.add(revidionsColumn());
		columns.add(messageColumn());
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

	private AbstractColumn<ReportItem, String> messageColumn() {
		return new AbstractColumn<ReportItem, String>(new Model<String>("message"), "message") {
			@Override
			public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
					IModel<ReportItem> rowModel) {
				cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
			}
		};
	}

	private AbstractColumn<ReportItem, String> revidionsColumn() {
		return new AbstractColumn<ReportItem, String>(new Model<String>("revision"), "revision") {
			@Override
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
		};
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

}
