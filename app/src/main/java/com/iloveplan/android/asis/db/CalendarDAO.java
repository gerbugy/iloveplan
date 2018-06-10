package com.iloveplan.android.asis.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.iloveplan.android.asis.MainApp;

public class CalendarDAO {

    private static CalendarDAO instance;

    private CalendarDAO() {

    }

    public static CalendarDAO getInstance() {
        if (instance == null)
            instance = new CalendarDAO();
        return instance;
    }

    public long insert(CalendarDVO dvo) {

        // 등록값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("DATE", dvo.getDate());
        values.put("DATE_LUNAR", dvo.getDateLunar());
        values.put("DATE_TXT", dvo.getDateTxt());
        values.put("HOLIDAY_YN", dvo.getHolidayYn());

        // 등록합니다.
        return MainApp.sqlite.insert("TB_CALENDAR", null, values);
    }

    public int update(CalendarDVO dvo) {

        // 변경값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("DATE_LUNAR", dvo.getDateLunar());
        values.put("DATE_TXT", dvo.getDateTxt());
        values.put("HOLIDAY_YN", dvo.getHolidayYn());

        // 변경합니다.
        return MainApp.sqlite.update("TB_CALENDAR", values, "DATE = ?", new String[] { String.valueOf(dvo.getDate()) });
    }

    public int delete(String startDt, String endDt) {
        return MainApp.sqlite.delete("TB_CALENDAR", "DATE BETWEEN ? AND ?", new String[] { startDt, endDt });
    }

    public CalendarDVO select(String date) {
        CalendarDVO dvo = null;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_CALENDAR WHERE DATE = ?", new String[] { date });
        if (cursor.moveToNext()) {
            dvo = new CalendarDVO();
            dvo.setDate(cursor.getString(cursor.getColumnIndex("DATE")));
            dvo.setDateLunar(cursor.getString(cursor.getColumnIndex("DATE_LUNAR")));
            dvo.setDateTxt(cursor.getString(cursor.getColumnIndex("DATE_TXT")));
            dvo.setHolidayYn(cursor.getString(cursor.getColumnIndex("HOLIDAY_YN")));
        }
        cursor.close();
        return dvo;
    }

    public ArrayList<CalendarDVO> selectList(String yyyy) {

        // 목록을 정의합니다.
        ArrayList<CalendarDVO> list = new ArrayList<CalendarDVO>();

        // 데이터를 조회합니다.
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_CALENDAR WHERE DATE LIKE ? ORDER BY DATE", new String[] { yyyy + "%" });

        // 데이터수만큼 반복합니다.
        while (cursor.moveToNext()) {

            // 데이터를 취득합니다.
            CalendarDVO dvo = new CalendarDVO();
            dvo.setDate(cursor.getString(cursor.getColumnIndex("DATE")));
            dvo.setDateLunar(cursor.getString(cursor.getColumnIndex("DATE_LUNAR")));
            dvo.setDateTxt(cursor.getString(cursor.getColumnIndex("DATE_TXT")));
            dvo.setHolidayYn(cursor.getString(cursor.getColumnIndex("HOLIDAY_YN")));

            // 리스트에 추가합니다.
            list.add(dvo);
        }

        // 커서를 종료합니다.
        cursor.close();

        // 목록을 반환합니다.
        return list;
    }
}
