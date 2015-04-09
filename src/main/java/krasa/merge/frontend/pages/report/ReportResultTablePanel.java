package krasa.merge.frontend.pages.report;

import java.util.*;

import krasa.core.frontend.commons.*;
import krasa.merge.backend.dto.*;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.markup.repeater.*;
import org.apache.wicket.model.*;
import org.tmatesoft.svn.core.SVNLogEntry;

public class ReportResultTablePanel extends Panel {

	public ReportResultTablePanel(String id, final IModel<List<SVNLogEntry>> model) {
		super(id, model);
		final ArrayList<IColumn<ReportItem, String>> columns = getColumns();
		AjaxFallbackDefaultDataTable<ReportItem, String> table = new AjaxFallbackDefaultDataTable<>("merges", columns,
				new ReportDataProvider2(model), 100);
		add(table);
	}

	private ArrayList<IColumn<ReportItem, String>> getColumns() {
		final ArrayList<IColumn<ReportItem, String>> columns = new ArrayList<>();
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
		return new AbstractColumn<ReportItem, String>(new Model<>("message"), "message") {

			@Override
			public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
					IModel<ReportItem> rowModel) {
				cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
			}
		};
	}

	private AbstractColumn<ReportItem, String> revidionsColumn() {
		return new AbstractColumn<ReportItem, String>(new Model<>("revision"), "revision") {

			@Override
			public void populateItem(Item<ICellPopulator<ReportItem>> cellItem, String componentId,
					IModel<ReportItem> rowModel) {
				ReportItem object = rowModel.getObject();
				RepeatingView links = new RepeatingView(componentId);
				for (SVNLogEntry commit : object.commits) {
					Fragment link = new Fragment(links.newChildId(), "linkFragment", ReportResultTablePanel.this);
					link.add(new FishEyeLink("link", new FishEyeLinkModel(commit.getRevision()),
							Model.of(commit.getRevision() + " " + getSubTaskAndMessage(commit)), new Model<>(
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
