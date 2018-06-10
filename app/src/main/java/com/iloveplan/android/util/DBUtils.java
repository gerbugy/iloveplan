package com.iloveplan.android.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class DBUtils {

    private DBUtils() {

    }

    public static boolean isExistsTable(SQLiteDatabase db, String tableName) {
        String sql = String.format("select * from sqlite_master where type = 'table' and name = '%s'", tableName);
        try (Cursor cursor = db.rawQuery(sql, null)) {
            return cursor.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
