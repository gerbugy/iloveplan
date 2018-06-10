package com.iloveplan.android.asis.view.plan.android.calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

public class Utils {

    public static final int MONDAY_BEFORE_JULIAN_EPOCH = Time.EPOCH_JULIAN_DAY - 3;

    public static int getJulianMondayFromWeeksSinceEpoch(int week) {
        return MONDAY_BEFORE_JULIAN_EPOCH + week * 7;
    }

    public static int getWeeksSinceEpochFromJulianDay(int julianDay, int firstDayOfWeek) {
        int diff = Time.THURSDAY - firstDayOfWeek;
        if (diff < 0) {
            diff += 7;
        }
        int refDay = Time.EPOCH_JULIAN_DAY - diff;
        return (julianDay - refDay) / 7;
    }

    public static String formatMonthYear(Context context, Time time) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;
        long millis = time.toMillis(true);
        return formatDateRange(context, millis, millis, flags);
    }

    public static String formatDateRange(Context context, long startMillis, long endMillis, int flags) {
        return DateUtils.formatDateRange(context, startMillis, endMillis, flags).toString();
    }

    public static String getTimeZone(Context context, Runnable callback) {
        return Time.getCurrentTimezone();
    }
}
