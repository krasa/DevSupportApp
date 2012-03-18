package krasa.frontend.pages.mergeinfo;

import krasa.backend.dto.MergeInfoResult;
import krasa.backend.dto.MergeInfoResultItem;
import krasa.frontend.pages.components.DateModel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResultPanel extends Panel {
    ModalWindow modal = new ModalWindow("modal");

    public MergeInfoResultPanel(String id, final List<MergeInfoResultItem> model) {
        super(id);
        modal.setParent(this);

        setOutputMarkupId(true);
        final ArrayList<IColumn<SVNLogEntry>> columns = new ArrayList<IColumn<SVNLogEntry>>();
        columns.add(new PropertyColumn<SVNLogEntry>(new Model<String>("revision"), "revision", "revision"));
        columns.add(new AbstractColumn<SVNLogEntry>(new Model<String>("message"), "message") {
            public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId, IModel<SVNLogEntry> rowModel) {
                cellItem.add(new MultiLineLabel(componentId, new PropertyModel<String>(rowModel, "message")));
            }
        });
        columns.add(new PropertyColumn<SVNLogEntry>(new Model<String>("author"), "author", "author"));
        columns.add(new AbstractColumn<SVNLogEntry>(new Model<String>("date"), "date") {
            public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId, IModel<SVNLogEntry> rowModel) {
                PropertyModel<Date> date = new PropertyModel<Date>(rowModel, "date");
                cellItem.add(new Label(componentId, new DateModel(date)));
            }
        });
//        columns.add(new AbstractColumn<SVNLogEntry>(new Model<String>("kuk"), "kuk") {
//            public void populateItem(Item<ICellPopulator<SVNLogEntry>> cellItem, String componentId, IModel<SVNLogEntry> rowModel) {
//                cellItem.add(new AjaxLink<SVNLogEntry>(componentId, rowModel){
//
//                    @Override
//                    public void onClick(AjaxRequestTarget target) {
//                        StringBuilder result = new StringBuilder();
//                        Map myChangedPaths = getModelObject().getChangedPaths();
//                        if (myChangedPaths != null && !myChangedPaths.isEmpty()) {
//                                    for (Iterator paths = myChangedPaths.values().iterator(); paths.hasNext();) {
//                                        result.append('\n');
//                                        SVNLogEntryPath path = (SVNLogEntryPath) paths.next();
//                                        result.append(path.toString());
//                                    }
//                                }
//                        modal.setContent(new MultiLineLabel(ModalWindow.CONTENT_ID, result.toString()));
//                        modal.show(target);
//                    }
//                });
//            }
//        });


        ListView<MergeInfoResultItem> components = new ListView<MergeInfoResultItem>("resultItem", model) {
            @Override
            protected void populateItem(ListItem<MergeInfoResultItem> components) {
                components.add(new Label("from", new PropertyModel<String>(components.getModel(), "from")));
                components.add(new Label("to", new PropertyModel<String>(components.getModel(), "to")));
                MergeInfoResultItem modelObject = components.getModelObject();
                AjaxFallbackDefaultDataTable<SVNLogEntry> table = new AjaxFallbackDefaultDataTable<SVNLogEntry>("merges", columns, new DataProvider(modelObject.getMerges()), 100);
                components.add(table);
            }
        };
        add(components);

    }

    public MergeInfoResultPanel(String mergeInfo, MergeInfoResult mergeInfo1) {
        this(mergeInfo, mergeInfo1.getMergeInfoResultItems());
    }

    private class DataProvider implements ISortableDataProvider<SVNLogEntry> {
        List<SVNLogEntry> merges;

        public DataProvider(List<SVNLogEntry> merges) {
            this.merges = merges;
        }

        public void detach() {
        }

        public Iterator<? extends SVNLogEntry> iterator(int first, int count) {
            return merges.iterator();
        }

        public int size() {
            return merges.size();
        }

        public IModel<SVNLogEntry> model(SVNLogEntry object) {
            return new Model<SVNLogEntry>(object);
        }

        public ISortState getSortState() {
            return new SingleSortState();
        }
    }
}
