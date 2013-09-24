package krasa.core.frontend.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Vojtech Krasa
 */
public class DateModel<T> extends Model<String> {
	public static final String DD_MM_YYYY_HH_MM = "dd.MM. HH:mm:ss";
	IModel<Date> date;
	public final SimpleDateFormat simpleDateFormat;

	public DateModel(IModel<Date> date) {
		simpleDateFormat = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		this.date = date;
	}

	public DateModel(IModel<Date> date, String pattern) {
		simpleDateFormat = new SimpleDateFormat(pattern);
		this.date = date;
	}

	@Override
	public String getObject() {
		Date object = date.getObject();
		if (object == null) {
			return "";
		}
		return simpleDateFormat.format(object);
	}

}
