package sk.hudak.prco.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";

    private DateUtils() {
    }

    public static final String formatDate(Date date) {
        return formatDate(date, DD_MM_YYYY_HH_MM_SS);

    }

    public static final String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
