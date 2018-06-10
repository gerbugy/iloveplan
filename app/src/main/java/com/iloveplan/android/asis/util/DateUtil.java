package com.iloveplan.android.asis.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * 현재시간을 기본문자열 형식으로 반환합니다.
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 현재시간을 포맷문자열 형식으로 반환합니다.
     */
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    /**
     * 문자열을 파싱합니다.
     */
    public static Date parse(String yyyyMMdd) {
        return parse(yyyyMMdd, "yyyyMMdd");
    }

    /**
     * 문자열을 파싱합니다.
     */
    public static Date parse(String str, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 시간을 포맷합니다.
     */
    public static String format(Timestamp timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(timestamp);
    }

    /**
     * 날짜형을 포맷합니다.
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    /**
     * 초를 포맷합니다.
     */
    public static String format(long milliseconds, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(milliseconds));
    }

    /**
     * 문자열을 포맷합니다.
     */
    public static String format(String yyyyMMdd, String formatPattern) {
        return format(yyyyMMdd, "yyyyMMdd", formatPattern);
    }

    /**
     * 문자열을 포맷합니다.
     */
    public static String format(String str, String parsePattern, String formatPattern) {
        return new SimpleDateFormat(formatPattern, Locale.getDefault()).format(parse(str, parsePattern));
    }

    /**
     * 두 날짜의 차이를 일단위로 반환합니다.
     */
    public static long diffOfDate(String begin, String end) {
        return ((parse(begin).getTime() - parse(end).getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 연연산을 수행후 반환합니다.
     */
    public static String addYear(String yyyyMMdd, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(yyyyMMdd, "yyyyMMdd"));
        cal.add(Calendar.YEAR, amount);
        return format(cal.getTime(), "yyyyMMdd");
    }

    /**
     * 일연산을 수행후 반환합니다.
     */
    public static String addDate(String yyyyMMdd, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(yyyyMMdd, "yyyyMMdd"));
        cal.add(Calendar.DATE, amount);
        return format(cal.getTime(), "yyyyMMdd");
    }

    /**
     * 월연산을 수행후 반환합니다.
     */
    public static String addMonth(String yyyyMMdd, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(yyyyMMdd, "yyyyMMdd"));
        cal.add(Calendar.MONTH, amount);
        return format(cal.getTime(), "yyyyMMdd");
    }
}
