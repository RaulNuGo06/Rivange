package com.rivange.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private DateTimeUtils() {
    }

    public static long parseDateMillis(String date) {
        DATE_FORMAT.setLenient(false);
        try {
            Date parsed = DATE_FORMAT.parse(date);
            return parsed != null ? parsed.getTime() : -1L;
        } catch (ParseException e) {
            return -1L;
        }
    }

    public static long parseDateTimeMillis(String date, String time) {
        DATE_TIME_FORMAT.setLenient(false);
        try {
            Date parsed = DATE_TIME_FORMAT.parse(date + " " + time);
            return parsed != null ? parsed.getTime() : -1L;
        } catch (ParseException e) {
            return -1L;
        }
    }

    public static boolean isPastDay(String date) {
        long dayMillis = parseDateMillis(date);
        if (dayMillis < 0) {
            return true;
        }
        Calendar selected = Calendar.getInstance();
        selected.setTimeInMillis(dayMillis);
        setDayStart(selected);

        Calendar today = Calendar.getInstance();
        setDayStart(today);

        return selected.before(today);
    }

    private static void setDayStart(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
