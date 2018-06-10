package com.iloveplan.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MemoDao {

    public static final String TABLE_NAME = "memo";

    public interface Columns {
        String _ID = "_id";
        String CONTENT = "content";
        String IMPORTANT = "important";
        String POSITION = "position";
        String CREATED_AT = "created_at";
        String UPDATED_AT = "updated_at";
    }

    private static MemoDao sInstance;

    private MemoDao() {

    }

    public static MemoDao getInstance() {
        if (sInstance == null) {
            sInstance = new MemoDao();
        }
        return sInstance;
    }

    public long insert(SQLiteItem item) {
        ContentValues values = new ContentValues();
        values.put(Columns.CONTENT, item.getString(Columns.CONTENT));
        values.put(Columns.IMPORTANT, item.getInt(Columns.IMPORTANT, 0));
        values.put(Columns.POSITION, newPosition());
        values.put(Columns.CREATED_AT, System.currentTimeMillis());
        return SQLiteHelper.getInstance().getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(SQLiteItem item) {
        ContentValues values = new ContentValues();
        values.put(Columns.CONTENT, item.getString(Columns.CONTENT));
        values.put(Columns.IMPORTANT, item.getInt(Columns.IMPORTANT));
        values.put(Columns.UPDATED_AT, System.currentTimeMillis());
        return SQLiteHelper.getInstance().getWritableDatabase().update(TABLE_NAME, values, "_id = ?", new String[]{item.getString(Columns._ID)});
    }

    public void updatePosition(SQLiteItem... items) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (SQLiteItem item : items) {
                values.put(Columns.POSITION, item.getInt(Columns.POSITION));
                db.update(TABLE_NAME, values, "_id = ?", new String[]{item.getString(Columns._ID)});
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
            item.put(Columns._ID, cursor.getLong(index++));
            item.put(Columns.CONTENT, cursor.getString(index++));
            item.put(Columns.IMPORTANT, cursor.getInt(index++));
            item.put(Columns.POSITION, cursor.getInt(index++));
            item.put(Columns.CREATED_AT, cursor.getLong(index++));
            item.put(Columns.UPDATED_AT, cursor.getLong(index));
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
            item.put(Columns._ID, cursor.getLong(index++));
            item.put(Columns.CONTENT, cursor.getString(index++));
            item.put(Columns.IMPORTANT, cursor.getInt(index++));
            item.put(Columns.POSITION, cursor.getInt(index++));
            item.put(Columns.CREATED_AT, cursor.getLong(index++));
            item.put(Columns.UPDATED_AT, cursor.getLong(index));
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
