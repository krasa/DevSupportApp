package krasa.smrt;

import java.util.*;

import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.*;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.*;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.gson.Gson;

//ssdfdw
public class SmrtMainPage extends BasePage {

	@SpringBean
	SmrtMonitoringImpl smrtMonitoring;
	List<ChoiceFilteredPropertyColumnModel<?, ?>> updateOnBeforeRender = new ArrayList<>();
	private SmrtFilter smrtFilter;

	public static void main(String[] args) {
		HashMap<Object, Object> src = new HashMap<>();
		src.put("sdf", "sd");
		src.put("sdfs", "sds");
		System.err.println(new Gson().toJson(src));
	}

	public SmrtMainPage() {
		centerColumn.add(new AttributeModifier("id", "center-column-wide"));
		final IModel<List<SmrtConnection>> smrtConnections = new SmrtConnectionsModel(this);
		smrtFilter = new SmrtFilter();
		List<IColumn<SmrtConnection, String>> iColumns = getColumns(smrtConnections);
		FilterForm<SmrtConnection> form = new FilterForm<>("form", smrtFilter);
		add(form);
		AjaxFallbackDefaultDataTable<SmrtConnection, String> table = new AjaxFallbackDefaultDataTable<>(
				"table", iColumns,
				getDataProvider(smrtConnections),
				200);
		form.add(table);
		table.addTopToolbar(new FilterToolbar(table, form));
	}

	private List<IColumn<SmrtConnection, String>> getColumns(IModel<List<SmrtConnection>> smrtConnections) {
		List<IColumn<SmrtConnection, String>> columns = new ArrayList<>();
		IModel<List<SmrtConnection>> byEnvironment = new SmrtFilteredConnectionsByEnvironment(smrtConnections,
				smrtFilter);

		ChoiceFilteredPropertyColumnModel<String, SmrtConnection> routingId = ColumnModels.choice(byEnvironment,
				"routingId");
		ChoiceFilteredPropertyColumnModel<String, SmrtConnection> systemId = ColumnModels.choice(byEnvironment,
				"systemId");
		ChoiceFilteredPropertyColumnModel<String, SmrtConnection> url = new ChoiceFilteredPropertyColumnModel<String, SmrtConnection>(
				byEnvironment, "url") {

			@Override
			protected boolean filter(SmrtConnection smrtConnection) {
				return smrtConnection.type.equals("outbound");
			}
		};
		updateOnBeforeRender.add(routingId);
		updateOnBeforeRender.add(systemId);
		updateOnBeforeRender.add(url);

		final ChoiceFilteredPropertyColumnModel<String, SmrtConnection> environments = new ChoiceFilteredPropertyColumnModel<String, SmrtConnection>(
				smrtConnections,
				"environment") {

			@Override
			public void reload() {
				super.reload();
				list.add("uat");
				Collections.sort(list);
			}
		};
		IModel<List<? extends String>> types = ColumnModels.choice(smrtConnections, "type");
		IModel<List<? extends String>> statuses = ColumnModels.choice(smrtConnections, "status");
		IModel<List<? extends String>> bindTypes = ColumnModels.choice(smrtConnections, "bindType");

		columns.add(Columns.<SmrtConnection> styled("environment", environments));
		columns.add(Columns.<SmrtConnection> styled("type", "type", types));
		columns.add(Columns.<SmrtConnection> column("routingId", routingId));
		columns.add(Columns.<SmrtConnection> styled("status", statuses));
		columns.add(Columns.<SmrtConnection> styled("bindType", bindTypes));
		columns.add(Columns.<SmrtConnection> column("systemId", systemId));
		columns.add(Columns.<SmrtConnection> column("url", url));
		columns.add(Columns.<SmrtConnection> column("lastBoundStartTime"));
		columns.add(Columns.<SmrtConnection> column("lastBoundEndTime"));
		columns.add(Columns.<SmrtConnection> column("reconnects"));
		return columns;
	}

	private SortableFilteredModelDataProvider<SmrtConnection> getDataProvider(
			IModel<List<SmrtConnection>> smrtFilteredConnections) {
		return new SortableFilteredModelDataProvider<>(smrtFilteredConnections, "type",
				SortOrder.DESCENDING, smrtFilter);
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		EmptyPanel components = new EmptyPanel(id);
		components.add(new AttributeModifier("id", "foo"));
		return components;
	}

	@Override
	protected void onBeforeRender() {
		for (ChoiceFilteredPropertyColumnModel<?, ?> choiceFilteredPropertyColumnModel : updateOnBeforeRender) {
			choiceFilteredPropertyColumnModel.reload();
		}
		super.onBeforeRender();
	}

	private class SmrtFilteredConnectionsByEnvironment extends LoadableDetachableModel<List<SmrtConnection>> {

		private IModel<List<SmrtConnection>> smrtConnections;
		private SmrtFilter filterStateLocator;

		public SmrtFilteredConnectionsByEnvironment(IModel<List<SmrtConnection>> smrtConnections,
				SmrtFilter filterStateLocator) {
			this.smrtConnections = smrtConnections;
			this.filterStateLocator = filterStateLocator;
		}

		@Override
		protected List<SmrtConnection> load() {
			return filterStateLocator.filterByEnvironment(smrtConnections.getObject());
		}
	}

}
