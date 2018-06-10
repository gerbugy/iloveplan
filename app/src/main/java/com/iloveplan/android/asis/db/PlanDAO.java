package com.iloveplan.android.asis.db;

import java.sql.Timestamp;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.iloveplan.android.asis.MainApp;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;

public class PlanDAO {

    private static PlanDAO instance;

    private PlanDAO() {

    }

    public static PlanDAO getInstance() {
        if (instance == null)
            instance = new PlanDAO();
        return instance;
    }

    public long insert(PlanDVO dvo) {

        // 전체건수를 설정합니다.
        dvo.setTotalCount(PlanUtil.calcTotalCountUntilToday(dvo));

        // 값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("PLAN_NM", dvo.getPlanNm());
        values.put("PLAN_STDT", dvo.getPlanStdt());
        values.put("PLAN_EDDT", dvo.getPlanEddt());
        values.put("TOTAL_COUNT", dvo.getTotalCount());
        values.put("SUCCESS_COUNT", dvo.getSuccessCount());
        values.put("ON_MON_YN", dvo.getOnMonYn());
        values.put("ON_TUE_YN", dvo.getOnTueYn());
        values.put("ON_WED_YN", dvo.getOnWedYn());
        values.put("ON_THU_YN", dvo.getOnThuYn());
        values.put("ON_FRI_YN", dvo.getOnFriYn());
        values.put("ON_SAT_YN", dvo.getOnSatYn());
        values.put("ON_SUN_YN", dvo.getOnSunYn());
        values.put("ON_HOLIDAY_YN", dvo.getOnHolidayYn());
        values.put("ORDER_NO", selectNewOrderNo());

        // 테이블에 반영합니다.
        return MainApp.sqlite.insert("TB_PLAN", null, values);
    }

    public int update(PlanDVO dvo) {

        // 전체건수를 설정합니다.
        dvo.setTotalCount(PlanUtil.calcTotalCountUntilToday(dvo));

        // 성공건수를 설정합니다.
        dvo.setSuccessCount(PlanDateDAO.getInstance().selectSuccessCount(dvo.getPlanNo()));

        // 변경값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("PLAN_NM", dvo.getPlanNm());
        values.put("PLAN_EDDT", dvo.getPlanEddt());
        values.put("TOTAL_COUNT", dvo.getTotalCount());
        values.put("SUCCESS_COUNT", dvo.getSuccessCount());
        values.put("ON_MON_YN", dvo.getOnMonYn());
        values.put("ON_TUE_YN", dvo.getOnTueYn());
        values.put("ON_WED_YN", dvo.getOnWedYn());
        values.put("ON_THU_YN", dvo.getOnThuYn());
        values.put("ON_FRI_YN", dvo.getOnFriYn());
        values.put("ON_SAT_YN", dvo.getOnSatYn());
        values.put("ON_SUN_YN", dvo.getOnSunYn());
        values.put("ON_HOLIDAY_YN", dvo.getOnHolidayYn());
        values.put("CHG_TS", DateUtil.getCurrentTime());

        // 변경합니다.
        return MainApp.sqlite.update("TB_PLAN", values, "PLAN_NO = ?", new String[] { String.valueOf(dvo.getPlanNo()) });
    }

    public PlanDVO select(String planNo) {
        PlanDVO dvo = null;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN WHERE PLAN_NO = ?", new String[] { planNo });
        if (cursor.moveToNext()) {
            dvo = new PlanDVO();
            dvo.setPlanNo(cursor.getInt(cursor.getColumnIndex("PLAN_NO")));
            dvo.setPlanNm(cursor.getString(cursor.getColumnIndex("PLAN_NM")));
            dvo.setPlanStdt(cursor.getString(cursor.getColumnIndex("PLAN_STDT")));
            dvo.setPlanEddt(cursor.getString(cursor.getColumnIndex("PLAN_EDDT")));
            dvo.setTotalCount(cursor.getInt(cursor.getColumnIndex("TOTAL_COUNT")));
            dvo.setSuccessCount(cursor.getInt(cursor.getColumnIndex("SUCCESS_COUNT")));
            dvo.setOnMonYn(cursor.getString(cursor.getColumnIndex("ON_MON_YN")));
            dvo.setOnTueYn(cursor.getString(cursor.getColumnIndex("ON_TUE_YN")));
            dvo.setOnWedYn(cursor.getString(cursor.getColumnIndex("ON_WED_YN")));
            dvo.setOnThuYn(cursor.getString(cursor.getColumnIndex("ON_THU_YN")));
            dvo.setOnFriYn(cursor.getString(cursor.getColumnIndex("ON_FRI_YN")));
            dvo.setOnSatYn(cursor.getString(cursor.getColumnIndex("ON_SAT_YN")));
            dvo.setOnSunYn(cursor.getString(cursor.getColumnIndex("ON_SUN_YN")));
            dvo.setOnHolidayYn(cursor.getString(cursor.getColumnIndex("ON_HOLIDAY_YN")));
            dvo.setOrderNo(cursor.getInt(cursor.getColumnIndex("ORDER_NO")));
            dvo.setRegTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("REG_TS"))));
            dvo.setChgTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("CHG_TS"))));
        }
        cursor.close();
        return dvo;
    }

    public ArrayList<PlanDVO> selectList(int listType) {

        // 목록을 정의합니다.
        ArrayList<PlanDVO> list = new ArrayList<PlanDVO>();

        // 데이터를 조회합니다.
        Cursor cursor = null;
        if (listType == ListType.ALL)
            cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN ORDER BY ORDER_NO", null);
        else if (listType == ListType.NOW)
            cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN WHERE PLAN_EDDT >= ? ORDER BY ORDER_NO", new String[] { DateUtil.getCurrentTime("yyyyMMdd") });
        else if (listType == ListType.NOW_WITH_YESTERDAY)
            cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN WHERE PLAN_EDDT >= ? ORDER BY ORDER_NO", new String[] { DateUtil.addDate(DateUtil.getCurrentTime("yyyyMMdd"), -1) });
        else if (listType == ListType.OLD)
            cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN WHERE PLAN_EDDT < ? ORDER BY ORDER_NO", new String[] { DateUtil.getCurrentTime("yyyyMMdd") });

        // 데이터수만큼 반복합니다.
        while (cursor.moveToNext()) {

            // 데이터를 취득합니다.
            PlanDVO dvo = new PlanDVO();
            dvo.setPlanNo(cursor.getInt(cursor.getColumnIndex("PLAN_NO")));
            dvo.setPlanNm(cursor.getString(cursor.getColumnIndex("PLAN_NM")));
            dvo.setPlanStdt(cursor.getString(cursor.getColumnIndex("PLAN_STDT")));
            dvo.setPlanEddt(cursor.getString(cursor.getColumnIndex("PLAN_EDDT")));
            dvo.setTotalCount(cursor.getInt(cursor.getColumnIndex("TOTAL_COUNT")));
            dvo.setSuccessCount(cursor.getInt(cursor.getColumnIndex("SUCCESS_COUNT")));
            dvo.setOnMonYn(cursor.getString(cursor.getColumnIndex("ON_MON_YN")));
            dvo.setOnTueYn(cursor.getString(cursor.getColumnIndex("ON_TUE_YN")));
            dvo.setOnWedYn(cursor.getString(cursor.getColumnIndex("ON_WED_YN")));
            dvo.setOnThuYn(cursor.getString(cursor.getColumnIndex("ON_THU_YN")));
            dvo.setOnFriYn(cursor.getString(cursor.getColumnIndex("ON_FRI_YN")));
            dvo.setOnSatYn(cursor.getString(cursor.getColumnIndex("ON_SAT_YN")));
            dvo.setOnSunYn(cursor.getString(cursor.getColumnIndex("ON_SUN_YN")));
            dvo.setOnHolidayYn(cursor.getString(cursor.getColumnIndex("ON_HOLIDAY_YN")));
            dvo.setOrderNo(cursor.getInt(cursor.getColumnIndex("ORDER_NO")));
            dvo.setRegTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("REG_TS"))));
            dvo.setChgTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("CHG_TS"))));

            // 리스트에 추가합니다.
            list.add(dvo);
        }

        // 커서를 종료합니다.
        cursor.close();

        // 목록을 반환합니다.
        return list;
    }

    public int delete(String planNo) {
        return MainApp.sqlite.delete("TB_PLAN", "PLAN_NO = ?", new String[] { planNo });
    }

    private int selectNewOrderNo() {
        int orderNo = 1;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT COALESCE(MAX(ORDER_NO), 0) + 1 FROM TB_PLAN WHERE PLAN_EDDT >= ? ", new String[] { DateUtil.getCurrentTime("yyyyMMdd") });
        if (cursor.moveToNext())
            orderNo = cursor.getInt(0);
        cursor.close();
        return orderNo;
    }

    public void updateOrderNo(ArrayList<PlanDVO> list) {
        MainApp.sqlite.beginTransaction();
        try {
            for (PlanDVO dvo : list)
                MainApp.sqlite.execSQL("UPDATE TB_PLAN SET ORDER_NO = ? WHERE PLAN_NO = ?", new Object[] { dvo.getOrderNo(), dvo.getPlanNo() });
            MainApp.sqlite.setTransactionSuccessful();
        } finally {
            MainApp.sqlite.endTransaction();
        }
    }

    public interface ListType {
        final int ALL = 0;
        final int NOW = 1;
        final int NOW_WITH_YESTERDAY = 2; // TODO all을 가져와서 처리하면 안될까? 헷갈린다.
        final int OLD = 3;
    }
}
