package krasa.frontend.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vojtech Krasa
 */
public class DateModel extends Model<String> {
    IModel<Date> date;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM");

    public DateModel(IModel<Date> date) {
        this.date = date;
    }

    @Override
    public String getObject() {
        return SIMPLE_DATE_FORMAT.format(date.getObject());
    }

}
