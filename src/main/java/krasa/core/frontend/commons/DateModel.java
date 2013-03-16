package krasa.core.frontend.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Vojtech Krasa
 */
public class DateModel<T> extends Model<String> {
	public static final String DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:MM";
	IModel<Date> date;
	public final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

	public DateModel(IModel<Date> date) {
		this.date = date;
	}

	@Override
	public String getObject() {
		Date object = date.getObject();
		if (object == null) {
			return "";
		}
		return SIMPLE_DATE_FORMAT.format(object);
	}

}
