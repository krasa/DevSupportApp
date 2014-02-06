package krasa.merge.frontend.component.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import krasa.core.frontend.commons.DateModel;
import krasa.core.frontend.commons.FishEyeLink;
import krasa.core.frontend.commons.FishEyeLinkModel;
import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.MergeService;
import krasa.merge.frontend.component.merge.DiffPanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogEntryTablePanel extends Panel {

	public static final String ROW_ID_PREFIX = "revision";
	@SpringBean
	protected MergeService mergeService;

	private IModel<MergeInfoResultItem> model;
	protected AjaxFallbackDefaultDataTable<SVNLogEntry, String> table;
	protected DiffModalWindow modalWindow;

	public SVNLogEntryTablePanel(String id, final IModel<MergeInfoResultItem> model) {
		super(id, model);
		this.model = model;
		final ArrayList<IColumn<SVNLogEntry, String>> columns = getColumns();
		final DataProvider dataProvider = new DataProvider(new AbstractReadOnlyModel<List<SVNLogEntry>>() {

			@Override
			public List<SVNLogEntry> getObject() {
				return model.getObject().getMerges();
			}
		});
		createTable(columns, dataProvider);
		Form form = new Form("form");
		add(form);
		form.add(table);

		add(modalWindow = new DiffModalWindow("modal1"));
	}

	protected void createTable(final ArrayList<IColumn<SVNLogEntry, String>> columns, final DataProvider dataProvider) {
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
								this.setVisible(false);
								target.add(this);
								event.stop();
							}
						}
					}
				};
				item.setMarkupId(ROW_ID_PREFIX + model.getObject().getRevision());
				item.setOutputMarkupId(true);
				return item;
			}
		};
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
		if (model.getObject().isMergeable()) {
			columns.add(new ButtonColumn<SVNLogEntry>(new ResourceModel("merge")) {

				@Override
				protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
					mergeService.merge(model.getObject(), revision.getObject());
					sendDeletedRowEvent(revision, target);

				}
			});
			columns.add(new ButtonColumn<SVNLogEntry>(new ResourceModel("mergeSvnMergeInfoOnly")) {

				@Override
				protected void onSubmit(IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
					mergeService.mergeSvnMergeInfoOnly(model.getObject(), revision.getObject());
					sendDeletedRowEvent(revision, target);
				}
			});
			columns.add(new ButtonColumn<SVNLogEntry>(new ResourceModel("showDiff")) {

				@Override
				protected void onSubmit(final IModel<SVNLogEntry> revision, AjaxRequestTarget target, Form<?> form) {
					modalWindow.setContent(new AjaxLazyLoadPanel(modalWindow.getContentId()) {

						@Override
						protected void onComponentLoaded(Component component, AjaxRequestTarget target) {
							super.onComponentLoaded(component, target);
							target.appendJavaScript("SyntaxHighlighter.highlight();");

						}

						@Override
						public Component getLazyLoadComponent(String markupId) {
							return new DiffPanel(markupId, modalWindow, revision, model) {

								@Override
								protected void onMerged(IModel<SVNLogEntry> revision, AjaxRequestTarget target) {
									SVNLogEntryTablePanel.this.sendDeletedRowEvent(revision, target);
								}
							};

						}
					});
					modalWindow.show(target);
				}
			});
		}
		return columns;
	}

	private void sendDeletedRowEvent(IModel<SVNLogEntry> model, AjaxRequestTarget target) {
		DeleteRowEvent<SVNLogEntry> componentDeletedEvent = new DeleteRowEvent<>(model.getObject());
		componentDeletedEvent.setTarget(target);
		send(table, Broadcast.DEPTH, componentDeletedEvent);
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
