package krasa.merge.frontend.component.table;

import java.util.*;

import krasa.core.frontend.commons.*;
import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.MergeService;
import krasa.merge.frontend.component.merge.DiffPanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.*;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.html.basic.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogEntryTablePanel extends Panel {

	private static final Logger log = LoggerFactory.getLogger(SVNLogEntryTablePanel.class);

	@SpringBean
	protected MergeService mergeService;

	protected AjaxFallbackDefaultDataTable<SVNLogEntry, String> table;
	protected FixedModalWindow modalWindow;
	private final MergeInfoResultItem mergeInfoResultItemWithoutMerges;
	private String rowPrefix;

	public SVNLogEntryTablePanel(String id, final IModel<MergeInfoResultItem> model) {
		super(id, model);
		mergeInfoResultItemWithoutMerges = new MergeInfoResultItem(model.getObject());
		rowPrefix = mergeInfoResultItemWithoutMerges.getFrom() + "->" + mergeInfoResultItemWithoutMerges.getTo();
		mergeInfoResultItemWithoutMerges.setMerges(null);// save memory
		ArrayList<IColumn<SVNLogEntry, String>> columns = getColumns();
		DataProvider dataProvider = new DataProvider(new AbstractReadOnlyModel<List<SVNLogEntry>>() {

			@Override
			public List<SVNLogEntry> getObject() {
				return model.getObject().getMerges();
			}
		});
		createTable(columns, dataProvider);
		Form form = new Form("form");
		add(form);
		form.add(table);

		add(modalWindow = new FixedModalWindow("modal1"));
	}

	protected void createTable(ArrayList<IColumn<SVNLogEntry, String>> columns, DataProvider dataProvider) {
		table = new AjaxFallbackDefaultDataTable<SVNLogEntry, String>("merges", columns, dataProvider, 100) {

			@Override
			protected Item<SVNLogEntry> newRowItem(String id, int index, IModel<SVNLogEntry> model) {
				Item<SVNLogEntry> item = new Item<SVNLogEntry>(id, index, model) {

					@Override
					public void onEvent(IEvent<?> event) {
						super.onEvent(event);
						if (event.getPayload() instanceof DeleteRowEvent) {
							DeleteRowEvent payload = (DeleteRowEvent) event.getPayload();
							SVNLogEntry logEntry = (SVNLogEntry) payload.getObject();
							AjaxRequestTarget target = payload.getTarget();
							if (logEntry.getRevision() == (getModelObject().getRevision())) {
								log.info("DeleteRowEvent revision {} matches, deleting.", logEntry.getRevision());
								this.setVisible(false);
								target.add(this);
								event.stop();
								log.info("DeleteRowEvent revision {} deleted.", getModelObject().getRevision());
							}
						}
					}
				};
				// must be unique across the whole page even with multiple tables
				item.setMarkupId(rowPrefix + "_revision" + model.getObject().getRevision());
				item.setOutputMarkupId(true);
				return item;
			}
		};
	}

	private ArrayList<IColumn<SVNLogEntry, String>> getColumns() {
		ArrayList<IColumn<SVNLogEntry, String>> columns = new ArrayList<>();
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
		if (mergeInfoResultItemWithoutMerges.isMergeable()) {
			columns.add(new MergeButton());
			columns.add(new MergeSvnMergeInfoOnlyButton());
			columns.add(new ShowDiffButton());
		}
		return columns;
	}

	private void sendDeletedRowEvent(AjaxRequestTarget target, SVNLogEntry object) {
		log.info("sendDeletedRowEvent start");
		DeleteRowEvent<SVNLogEntry> componentDeletedEvent = new DeleteRowEvent<>(object);
		componentDeletedEvent.setTarget(target);
		send(table, Broadcast.DEPTH, componentDeletedEvent);
		log.info("sendDeletedRowEvent end");
	}

	private class DataProvider implements ISortableDataProvider<SVNLogEntry, String> {

		IModel<List<SVNLogEntry>> listIModel;

		public DataProvider(IModel<List<SVNLogEntry>> listIModel) {
			this.listIModel = listIModel;
		}

		@Override
		public void detach() {
		}

		@Override
		public Iterator<? extends SVNLogEntry> iterator(long first, long count) {
			return listIModel.getObject().iterator();
		}

		@Override
		public long size() {
			return listIModel.getObject().size();
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

	private class MergeSvnMergeInfoOnlyButton extends ButtonColumn<SVNLogEntry> {

		public MergeSvnMergeInfoOnlyButton() {
			super(new ResourceModel("mergeSvnMergeInfoOnly"));
		}

		@Override
		protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
			mergeService.mergeSvnMergeInfoOnly(mergeInfoResultItemWithoutMerges, revision.getObject());
			sendDeletedRowEvent(target, revision.getObject());
		}
	}

	private class ShowDiffButton extends ButtonColumn<SVNLogEntry> {

		public ShowDiffButton() {
			super(new ResourceModel("showDiff"));
		}

		@Override
		protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
			final SVNLogEntry revisionObject = revision.getObject();
			modalWindow.setContent(new AjaxLazyLoadPanel(modalWindow.getContentId()) {

				@Override
				protected void onComponentLoaded(Component component, AjaxRequestTarget target) {
					super.onComponentLoaded(component, target);
					target.appendJavaScript("SyntaxHighlighter.highlight();");

				}

				@Override
				public Component getLazyLoadComponent(String markupId) {
					return new DiffPanel(markupId, modalWindow, mergeInfoResultItemWithoutMerges, revisionObject) {

						@Override
						protected void onMerged(SVNLogEntry revision, AjaxRequestTarget target) {
							SVNLogEntryTablePanel.this.sendDeletedRowEvent(target, revision);
						}
					};

				}
			});
			modalWindow.show(target);
		}
	}

	private class MergeButton extends ButtonColumn<SVNLogEntry> {

		public MergeButton() {
			super(new ResourceModel("merge"));
		}

		@Override
		protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
			SVNLogEntry svnLogEntry = revision.getObject();
			log.info("merge " + svnLogEntry);
			mergeService.merge(mergeInfoResultItemWithoutMerges, svnLogEntry);
			sendDeletedRowEvent(target, svnLogEntry);
		}
	}
}
