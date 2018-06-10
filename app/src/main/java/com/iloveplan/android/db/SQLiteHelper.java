package com.iloveplan.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sInstance;

    private SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void initialize(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        sInstance = new SQLiteHelper(context, name, factory, version);
    }

    public static SQLiteHelper getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createMemo(db);
            createAccount(db);
            createTables(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createMemo(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS memo (_id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, important INTEGER NOT NULL DEFAULT 0, position INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL DEFAULT 0, updated_at INTEGER NOT NULL DEFAULT 0)");
    }

    private void createAccount(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS account (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, login_id TEXT, login_password TEXT, url TEXT, description TEXT, position INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL DEFAULT 0, updated_at INTEGER NOT NULL DEFAULT 0)");
    }

    private void createTables(SQLiteDatabase db) {

        StringBuffer sql = null;

        // 날짜
        sql = new StringBuffer();
        sql.append(" CREATE TABLE IF NOT EXISTS TB_CALENDAR (                                                               ");
        sql.append("      DATE              VARCHAR(8)                                                                      ");
        sql.append("    , DATE_LUNAR        VARCHAR(8)      NOT NULL                                                        ");
        sql.append("    , DATE_TXT          TEXT                                                                            ");
        sql.append("    , HOLIDAY_YN        CHAR(1)         NOT NULL    DEFAULT 'N'                                         ");
        sql.append("    , PRIMARY KEY(DATE)                                                                                 ");
        sql.append(" );                                                                                                     ");
        db.execSQL(sql.toString());

        // 계획
        sql = new StringBuffer();
        sql.append(" CREATE TABLE IF NOT EXISTS TB_PLAN (                                                                   ");
        sql.append("      PLAN_NO           INTEGER         PRIMARY KEY AUTOINCREMENT                                       ");
        sql.append("    , PLAN_NM           VARCHAR(100)    NOT NULL                                                        ");
        sql.append("    , PLAN_STDT         VARCHAR(8)      NOT NULL                                                        ");
        sql.append("    , PLAN_EDDT         VARCHAR(8)      NOT NULL                                                        ");
        sql.append("    , TOTAL_COUNT       INTEGER         NOT NULL                                                        ");
        sql.append("    , SUCCESS_COUNT     INTEGER         NOT NULL                                                        ");
        sql.append("    , ON_MON_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_TUE_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_WED_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_THU_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_FRI_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_SAT_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_SUN_YN         CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ON_HOLIDAY_YN     CHAR(1)         NOT NULL    DEFAULT 'Y'                                         ");
        sql.append("    , ORDER_NO          INTEGER         NOT NULL    DEFAULT 0                                           ");
        sql.append("    , REG_TS            DATETIME        NOT NULL    DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))  ");
        sql.append("    , CHG_TS            DATETIME        NOT NULL    DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))  ");
        sql.append(" );                                                                                                     ");
        db.execSQL(sql.toString());

        // 계획일별
        sql = new StringBuffer();
        sql.append(" CREATE TABLE IF NOT EXISTS TB_PLAN_DATE (                                                              ");
        sql.append("      PLAN_NO           INTEGER                                                                         ");
        sql.append("    , PLAN_DT           VARCHAR(8)      NOT NULL                                                        ");
        sql.append("    , SUCCESS_YN        CHAR(1)         NOT NULL    DEFAULT 'N'                                         ");
        sql.append("    , MEMO_TXT          TEXT                                                                            ");
        sql.append("    , REG_TS            DATETIME        NOT NULL    DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))  ");
        sql.append("    , CHG_TS            DATETIME        NOT NULL    DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))  ");
        sql.append("    , PRIMARY KEY(PLAN_NO, PLAN_DT)                                                                     ");
        sql.append(" );                                                                                                     ");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            switch (newVersion) {
                case 18030604:
                    upgrade_18030604(db);
                case 18030605:
                    upgrade_18030605(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void upgrade_18030604(SQLiteDatabase db) {

    }

    private void upgrade_18030605(SQLiteDatabase db) {

    }
}
