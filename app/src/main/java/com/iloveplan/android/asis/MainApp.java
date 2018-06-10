package com.iloveplan.android.asis;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.iloveplan.android.Constants;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;
import com.iloveplan.android.db.SQLiteHelper;
import com.iloveplan.android.util.AppUtils;

import java.util.Calendar;

public final class MainApp extends Application {

    public static SQLiteDatabase sqlite;

    // 설정변경리스너입니다.
    private SharedPreferences mSharedPreferences;
    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // 계획건수정보를 업데이트합니다.
            // 전원오프상태에서 자정을 넘기면 알람을 통한 처리가 불가하므로 여기에서 처리해야 합니다.
            if (key.equals(Settings.PREF_TODAY))
                PlanUtil.updateTotalAndSuccessCount(getApplicationContext());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteHelper.initialize(this, Constants.DATABASE_NAME, null, AppUtils.getPackageInfo(this).versionCode);
        SQLiteHelper.getInstance().getReadableDatabase(); // 데이터베이스 생성 또는 업그레이드를 수행합니다.

        ////////////////////////////////////////////////////////////////////////////////////////////
        // 데이터베이스 연결객체를 취득합니다.
        closeAndOpenDatabase(this);

        // 설정변경리스너를 등록합니다.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        // 설정객체를 초기화합니다.
        // 설정변경리스너를 등록한 후에 수행합니다.
        initPreferences();

        // 시간변경리시버를 설정합니다.
        registerTimeChangesReceiver();

        // 자정알람을 설정합니다.
        setMidnightAlarm();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sqlite.close();
        unregisterReceiver(mTimeChangedReceiver);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    /**
     * 데이터베이스 연결객체를 설정합니다.
     */
    public static void closeAndOpenDatabase(Context context) {
        if (sqlite != null && sqlite.isOpen())
            sqlite.close();
        sqlite = SQLiteHelper.getInstance().getWritableDatabase();
    }

    /**
     * 설정정보에 초기값을 설정합니다.
     */
    private void initPreferences() {

        // 설정객체를 취득합니다.
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = preference.edit();

        // 초기값을 설정합니다.
        editor.putString(Settings.PREF_TODAY, DateUtil.getCurrentTime("yyyyMMdd"));
        if (!preference.contains(Settings.PREF_PLAN_BATCHED_TIME))
            editor.putString(Settings.PREF_PLAN_BATCHED_TIME, "00000000000000");
        if (!preference.contains(Settings.PREF_CALENDAR_VERSION))
            editor.putInt(Settings.PREF_CALENDAR_VERSION, -1);

        // 설정정보를 커밋합니다.
        editor.commit();
    }

    /**
     * 시간변경리시버를 설정합니다.
     */
    private void registerTimeChangesReceiver() {

        // 필터를 정의합니다.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);

        // 리시버를 등록합니다.
        registerReceiver(mTimeChangedReceiver, filter);
    }

    /**
     * 시간변경리시버입니다.
     */
    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED) || action.equals(Intent.ACTION_LOCALE_CHANGED)) {

                // 설정객체의 날짜정보를 업데이트합니다.
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Settings.PREF_TODAY, DateUtil.getCurrentTime("yyyyMMdd")).commit();

                // 자정알람을 설정합니다.
                setMidnightAlarm();
            }
        }
    };

    /**
     * 자정알람을 설정합니다.
     */
    private void setMidnightAlarm() {

        // 자정을 설정합니다.
        Calendar midnight = Calendar.getInstance();
        midnight.add(Calendar.DATE, 1); // 주석처리하면 알람등록과 동시에 즉시 실행됩니다.
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        // 반복알람을 설정합니다.
        // 오늘 자정부터 실행됩니다.
        Intent intent = new Intent(this, MidnightAlarmReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, midnight.getTimeInMillis(), 24 * 60 * 60 * 1000, operation);
    }
}