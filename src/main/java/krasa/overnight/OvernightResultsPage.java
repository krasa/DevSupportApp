package krasa.overnight;

import java.util.*;

import krasa.core.frontend.commons.*;
import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.pages.BasePage;
import krasa.overnight.domain.Result;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.*;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class OvernightResultsPage extends BasePage {

	private final IModel<List<Result>> model;
	private final AjaxFallbackDefaultDataTable<Result, String> table;
	private final AjaxFallbackDefaultDataTable<Result, String> differenceFromTable;
	private final IModel<List<Result>> fromModel;
	@SpringBean
	OvernightFacade overnightFacade;
	private Date date = new Date();
	private Date differenceFromDate = DateUtils.addDays(new Date(), -1);

	public OvernightResultsPage() {
		Form form = new Form("form");
		queue(form);
		DateTextField df = new DateTextField("date", new PropertyModel<Date>(this, "date"), "dd.MM.yyyy");
		df.add(new DatePicker());
		df.add(new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(table);
				target.add(differenceFromTable);
			}
		});
		DateTextField differenceFromDateField = new DateTextField("differenceFromDate", new PropertyModel<Date>(this,
				"differenceFromDate"), "dd.MM.yyyy");
		differenceFromDateField.add(new DatePicker());
		differenceFromDateField.add(new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(differenceFromTable);
			}
		});
		form.add(df);
		form.add(differenceFromDateField);
		model = getModel();
		fromModel = getFromModel();

		ArrayList<IColumn<Result, String>> iColumns = getiColumns();

		table = new AjaxFallbackDefaultDataTable<>("table", iColumns, getSortableDataProvider(), Integer.MAX_VALUE);
		queue(table);
		differenceFromTable = new AjaxFallbackDefaultDataTable<>("differenceFromTable", iColumns,
				getDifferenceSortableDataProvider(), 100);
		form.add(differenceFromTable);

	}

	private ISortableDataProvider<Result, String> getDifferenceSortableDataProvider() {
		return new SortableDataProvider<Result, String>() {

			@Override
			public Iterator<Result> iterator(long first, long count) {

				ListIterator<Result> listIterator = fromModel.getObject().listIterator((int) first);
				return listIterator;
			}

			@Override
			public long size() {
				List<Result> object = fromModel.getObject();
				return object.size();
			}

			@Override
			public IModel<Result> model(Result object) {
				return new OvernightDbModel(object);
			}

			@Override
			public void detach() {
				super.detach();
				fromModel.detach();
			}
		};
	}

	protected SortableDataProvider<Result, String> getSortableDataProvider() {
		return new SortableDataProvider<Result, String>() {

			@Override
			public Iterator<Result> iterator(long first, long count) {
				List<Result> object = model.getObject();
				ListIterator<Result> listIterator = object.listIterator((int) first);
				return listIterator;
			}

			@Override
			public long size() {
				List<Result> object = model.getObject();
				return object.size();
			}

			@Override
			public IModel<Result> model(Result object) {
				return new OvernightDbModel(object);
			}

			@Override
			public void detach() {
				super.detach();
				model.detach();
			}
		};
	}

	protected ArrayList<IColumn<Result, String>> getiColumns() {
		ArrayList<IColumn<Result, String>> iColumns = new ArrayList<>();
		iColumns.add(new PanelColumn<Result>(new Model<>("result"), "result") {

			@Override
			protected Panel getPanel(String componentId, IModel<Result> rowModel) {
				return new LabelPanel(componentId, new PropertyModel<>(rowModel, "result.value")) {

					@Override
					protected Component getComponent(String id, IModel labelModel) {
						return new StyledLabel(id, labelModel);
					}
				};
			}
		});
		iColumns.add(new PropertyColumn<Result, String>(new Model<String>("component"), "testName.component.name"));
		iColumns.add(new PropertyColumn<Result, String>(new Model<String>("country"), "country.code"));
		iColumns.add(new LinkColumn<Result, String>(new Model<String>("name"), "name") {

			@Override
			protected AbstractLink getLinkComponent(String id, IModel<String> labelModel, IModel<Result> rowModel) {
				Result result = rowModel.getObject();

				String date = org.apache.commons.lang3.time.FastDateFormat.getInstance("yyyy-MM-dd").format(
						DateUtils.addHours(result.getTimeStamp(), -OvernightDao.OFFSET));

				String environment = result.getTargetEnvironment().getName();
				String testNameName = result.getTestName().getName();
				String component = result.getTestName().getComponent().getName();
				String name = result.getName();
				String href;
				if (result.getTestingTool().getName().equals("soapui")) {
					switch (component) {
					case "dcb": {
						component = "DCB3_TEST_";
					}
					}
					component = component + result.getCountry().getCode().toUpperCase();
					href = getSoapUiUrl(result, date, environment, component, name);
				} else {
					href = getAutotestUrl(result, date, environment, component, name);
				}

				return new ExternalLink(id, href, testNameName);
			}

		});
		iColumns.add(new PropertyColumn<Result, String>(new Model<String>("target"), "targetEnvironment.name"));
		iColumns.add(new PropertyColumn<Result, String>(new Model<String>("injector"), "injectorEnvironment.name"));
		iColumns.add(new DateColumn<Result>(new Model<String>("time"), "timeStamp"));
		;
		;

		return iColumns;
	}

	private String getAutotestUrl(Result result, String date, String environment, String component, String name) {
		String packageName;
		packageName = "overnight_" + component + "_all-" + result.getTargetEnvironment().getName() + "-stub-"
				+ result.getCountry().getCode() + "-all";

		String href = getRoot(result) + date + "/" + environment + "/" + packageName;
		return href;
	}

	private String getSoapUiUrl(Result result, String date, String environment, String component, String name) {
		String href;
		String packageName = "";
		String postfix;
		if (result.getTestName().getComponent().getName().equals("ssa")) {
			postfix = "";
		} else if (result.getTestName().getName().contains("Subscription")) {
			postfix = "";
		} else {

			if (name.startsWith("GET_out")) {
				packageName = "GUT_OUT";
			} else if (name.startsWith("R10_Echo")) {
				packageName = "echo";
			} else if (name.startsWith("R10_GetProvisioning")) {
				packageName = "getProvisioning";
			} else if (name.startsWith("R10_Auth")) {
				packageName = "auth";
			} else if (name.contains("CancelNo")) {
				packageName = "cancelNotification";
			} else if (name.startsWith("TestCase")) {
				packageName = "invalid_calls";
			} else if (name.startsWith("run_DCB_processor")) {
				packageName = "Run_DCB_processor";
			} else if (name.startsWith("run_DCB_processor")) {
				packageName = "Run_DCB_processor";
			}
			if (packageName.length() > 0) {
				packageName = packageName + "/";
			}
			postfix = "/" + component + "/" + packageName + name + "/";
		}

		href = getRoot(result) + date + "/" + environment + postfix;
		return href;
	}

	private String getRoot(Result result) {
		String name = result.getInjectorEnvironment().getName();
		if (name.equals("autotest")) {
			name = "";
		} else {
			name = name + ".";
		}
		return "http://autotest." + name + "tmdev/::OVERNIGHTS::/";
	}

	protected LoadableDetachableModel<List<Result>> getModel() {
		return new LoadableDetachableModel<List<Result>>() {

			@Override
			protected List<Result> load() {
				return overnightFacade.getResults(DateUtils.truncate(date, Calendar.DAY_OF_MONTH), 1000);
			}
		};
	}

	protected IModel<List<Result>> getFromModel() {
		return new LoadableDetachableModel<List<Result>>() {

			@Override
			protected List<Result> load() {
				List<Result> from = overnightFacade.getResults(
						DateUtils.truncate(differenceFromDate, Calendar.DAY_OF_MONTH), 1000);

				Set<String> keys = new HashSet<>();
				for (Result result : from) {
					keys.add(getKey(result));
				}

				List<Result> results = new ArrayList<>();
				for (Result result : model.getObject()) {
					if (!keys.contains(getKey(result))) {
						results.add(result);
					}
				}

				return results;
			}
		};
	}

	protected String getKey(Result result) {
		return result.getTestName().getComponent().getName() + result.getTargetEnvironment().getName()
				+ result.getCountry().getCode() + result.getName();
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		EmptyPanel components = new EmptyPanel(id);
		components.setVisible(false);
		return components;
	}
}
