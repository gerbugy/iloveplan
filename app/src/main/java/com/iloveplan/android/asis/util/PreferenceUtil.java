package com.iloveplan.android.asis.util;

import java.util.Map;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceUtil {

    public static void printAll(Context context) {
        Map<String, ?> keys = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("TEST", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}
