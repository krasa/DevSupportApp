package krasa.merge.frontend.component.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import krasa.core.frontend.commons.DateModel;
import krasa.core.frontend.commons.FishEyeLink;
import krasa.core.frontend.commons.FishEyeLinkModel;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.SvnMergeService;

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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogEntryTablePanel extends Panel {

	@SpringBean
	SvnMergeService svnMergeService;
	private IModel<MergeInfoResultItem> model;

	public SVNLogEntryTablePanel(String id, final IModel<MergeInfoResultItem> model) {
		super(id, model);
		this.model = model;
		final ArrayList<IColumn<SVNLogEntry, String>> columns = getColumns();
		AjaxFallbackDefaultDataTable<SVNLogEntry, String> table = new AjaxFallbackDefaultDataTable<>("merges", columns,
				new DataProvider(new AbstractReadOnlyModel<List<SVNLogEntry>>() {

					@Override
					public List<SVNLogEntry> getObject() {
						return model.getObject().getMerges();
					}
				}), 100);
		Form form = new Form("form");
		add(form);
		form.add(table);
	}

	private ArrayList<IColumn<SVNLogEntry, String>> getColumns() {
		final ArrayList<IColumn<SVNLogEntry, String>> columns = new ArrayList<>();
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<>("revision"), "revision") {

			@Override
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				long revision = rowModel.getObject().getRevision();
				Fragment link = new Fragment(componentId, "linkFragment", SVNLogEntryTablePanel.this);
				link.add(new FishEyeLink("link", new FishEyeLinkModel(revision), Model.of(revision)));
				cellItem.add(link);
			}
		});
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<>("message"), "message") {

			@Override
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
			}
		});
		columns.add(new PropertyColumn<SVNLogEntry, String>(new Model<>("author"), "author", "author"));
		columns.add(new AbstractColumn<SVNLogEntry, String>(new Model<>("date"), "date") {

			@Override
			public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId,
					IModel<SVNLogEntry> rowModel) {
				PropertyModel<Date> date = new PropertyModel<>(rowModel, "date");
				cellItem.add(new Label(componentId, new DateModel(date)));
			}
		});
		// if (model.getObject().isMergeable()) {
		// columns.add(new ButtonColumn<SVNLogEntry>(new Model<String>("merge")) {
		//
		// @Override
		// protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
		// svnMergeService.merge(model.getObject(), revision.getObject());
		// }
		// });
		// }
		return columns;
	}

	private class DataProvider implements ISortableDataProvider<SVNLogEntry, String> {

		IModel<List<SVNLogEntry>> model;

		public DataProvider(IModel<List<SVNLogEntry>> model) {
			this.model = model;
		}

		@Override
		public void detach() {
		}

		@Override
		public Iterator<? extends SVNLogEntry> iterator(long first, long count) {
			return model.getObject().iterator();
		}

		@Override
		public long size() {
			return model.getObject().size();
		}

		@Override
		public IModel<SVNLogEntry> model(SVNLogEntry object) {
			return new Model<>(object);
		}

		@Override
		public ISortState getSortState() {
			return new SingleSortState();
		}
	}
}
