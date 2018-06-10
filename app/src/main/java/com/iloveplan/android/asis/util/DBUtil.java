package com.iloveplan.android.asis.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {

    public static boolean isExistsTable(SQLiteDatabase db, String tableName) {

        // 커서를 선언합니다.
        Cursor cursor = null;

        try {

            // 쿼리문을 작성합니다.
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT NAME           ");
            sql.append("   FROM SQLITE_MASTER  ");
            sql.append("  WHERE TYPE = ?       ");
            sql.append("    AND NAME = ?       ");

            // 쿼리문을 실행합니다.
            cursor = db.rawQuery(sql.toString(), new String[] { "table", tableName });

            // 결과값을 반환합니다.
            return cursor.moveToNext();

        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
