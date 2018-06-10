package com.iloveplan.android.db;

import java.util.HashMap;

public class SQLiteItem extends HashMap<String, Object> {

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        if (containsKey(key)) {
            Object value = get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return defaultValue;
    }

    public long getLong(String key) {
        return Long.parseLong(get(key).toString());
    }

    public long getLong(String key, long defaultValue) {
        if (containsKey(key)) {
            Object value = get(key);
            if (value != null) {
                return Long.parseLong(value.toString());
            }
        }
        return defaultValue;
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key).toString());
    }

    public int getInt(String key, int defaultValue) {
        if (containsKey(key)) {
            Object value = get(key);
            if (value != null) {
                return Integer.parseInt(value.toString());
            }
        }
        return defaultValue;
    }
}
