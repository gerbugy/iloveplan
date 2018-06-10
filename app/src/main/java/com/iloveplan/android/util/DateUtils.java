package com.iloveplan.android.util;

import android.icu.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {

    }

    public static String currentTime(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    public static CharSequence formatSameDayTime(long when) {
        int format = android.text.format.DateUtils.isToday(when) ? DateFormat.MEDIUM : DateFormat.LONG;
        return android.text.format.DateUtils.formatSameDayTime(when, System.currentTimeMillis(), format, format);
    }
}
