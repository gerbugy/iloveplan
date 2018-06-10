package com.iloveplan.android.asis.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iloveplan.android.db.MemoDao;
import com.iloveplan.android.db.SQLiteHelper;
import com.iloveplan.android.db.SQLiteItem;

import java.util.ArrayList;
import java.util.List;

public class HabitDao {

    public static final String TABLE_NAME = "TB_PLAN";

    public interface Columns {
        String _ID = "_id";
        String CONTENT = "content";
        String IMPORTANT = "important";
        String POSITION = "position";
        String CREATED_AT = "created_at";
        String UPDATED_AT = "updated_at";

        ////////////////////////////////////////
        String PLAN_NO = "PLAN_NO";
        String PLAN_NM = "PLAN_NM";
        String PLAN_STDT = "PLAN_STDT";
        String PLAN_EDDT = "PLAN_EDDT";
        String TOTAL_COUNT = "TOTAL_COUNT";
        String SUCCESS_COUNT = "SUCCESS_COUNT";
        String ON_MON_YN = "ON_MON_YN";
        String ON_TUE_YN = "ON_TUE_YN";
        String ON_WED_YN = "ON_WED_YN";
        String ON_THU_YN = "ON_THU_YN";
        String ON_FRI_YN = "ON_FRI_YN";
        String ON_SAT_YN = "ON_SAT_YN";
        String ON_SUN_YN = "ON_SUN_YN";
        String ON_HOLIDAY_YN = "ON_HOLIDAY_YN";
        String ORDER_NO = "ORDER_NO";
        String REG_TS = "REG_TS";
        String CHG_TS = "CHG_TS";
    }

    private static HabitDao sInstance;

    private HabitDao() {

    }

    public static HabitDao getInstance() {
        if (sInstance == null) {
            sInstance = new HabitDao();
        }
        return sInstance;
    }

    public long insert(SQLiteItem item) {
        ContentValues values = new ContentValues();
        values.put(MemoDao.Columns.CONTENT, item.getString(MemoDao.Columns.CONTENT));
        values.put(MemoDao.Columns.IMPORTANT, item.getInt(MemoDao.Columns.IMPORTANT, 0));
        values.put(MemoDao.Columns.POSITION, newPosition());
        values.put(MemoDao.Columns.CREATED_AT, System.currentTimeMillis());
        return SQLiteHelper.getInstance().getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(SQLiteItem item) {
        ContentValues values = new ContentValues();
        values.put(MemoDao.Columns.CONTENT, item.getString(MemoDao.Columns.CONTENT));
        values.put(MemoDao.Columns.IMPORTANT, item.getInt(MemoDao.Columns.IMPORTANT));
        values.put(MemoDao.Columns.UPDATED_AT, System.currentTimeMillis());
        return SQLiteHelper.getInstance().getWritableDatabase().update(TABLE_NAME, values, "_id = ?", new String[]{item.getString(MemoDao.Columns._ID)});
    }

    public void updatePosition(SQLiteItem... items) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (SQLiteItem item : items) {
                values.put(MemoDao.Columns.POSITION, item.getInt(MemoDao.Columns.POSITION));
                db.update(TABLE_NAME, values, "_id = ?", new String[]{item.getString(MemoDao.Columns._ID)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int delete(long _id) {
        return SQLiteHelper.getInstance().getWritableDatabase().delete(TABLE_NAME, "_id = ?", new String[]{String.valueOf(_id)});
    }

    public SQLiteItem select(long _id) {
        SQLiteItem item = null;
        Cursor cursor = SQLiteHelper.getInstance().getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE _id = ?", new String[]{String.valueOf(_id)});
        if (cursor.moveToNext()) {
            int index = 0;
            item = new SQLiteItem();
            item.put(MemoDao.Columns._ID, cursor.getLong(index++));
            item.put(MemoDao.Columns.CONTENT, cursor.getString(index++));
            item.put(MemoDao.Columns.IMPORTANT, cursor.getInt(index++));
            item.put(MemoDao.Columns.POSITION, cursor.getInt(index++));
            item.put(MemoDao.Columns.CREATED_AT, cursor.getLong(index++));
            item.put(MemoDao.Columns.UPDATED_AT, cursor.getLong(index));
        }
        cursor.close();
        return item;
    }

    public List<SQLiteItem> selectList(String whereClause, String orderClause, String limitClause) {
        List<SQLiteItem> items = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE_NAME);
        if (!TextUtils.isEmpty(whereClause))
            sql.append(" WHERE ").append(whereClause);
        if (!TextUtils.isEmpty(orderClause))
            sql.append(" ORDER BY ").append(orderClause);
        if (!TextUtils.isEmpty(limitClause))
            sql.append(" LIMIT ").append(limitClause);

        Cursor cursor = SQLiteHelper.getInstance().getReadableDatabase().rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            int index = 0;
            SQLiteItem item = new SQLiteItem();
            item.put(MemoDao.Columns._ID, cursor.getLong(index++));
            item.put(MemoDao.Columns.CONTENT, cursor.getString(index++));
            item.put(MemoDao.Columns.IMPORTANT, cursor.getInt(index++));
            item.put(MemoDao.Columns.POSITION, cursor.getInt(index++));
            item.put(MemoDao.Columns.CREATED_AT, cursor.getLong(index++));
            item.put(MemoDao.Columns.UPDATED_AT, cursor.getLong(index));
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public int selectCount(String whereClause) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM " + TABLE_NAME);
        if (!TextUtils.isEmpty(whereClause))
            sql.append(" WHERE ").append(whereClause);
        Cursor cursor = SQLiteHelper.getInstance().getReadableDatabase().rawQuery(sql.toString(), null);
        if (cursor.moveToNext())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private int newPosition() {
        int position = 1;
        Cursor cursor = SQLiteHelper.getInstance().getReadableDatabase().rawQuery("SELECT COALESCE(MAX(position), 0) + 1 FROM " + TABLE_NAME, null);
        if (cursor.moveToNext())
            position = cursor.getInt(0);
        cursor.close();
        return position;
    }
}
