package com.iloveplan.android.asis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.iloveplan.android.asis.util.DateUtil;

public class MidnightAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Settings.PREF_TODAY, DateUtil.getCurrentTime("yyyyMMdd")).commit();
    }
}
