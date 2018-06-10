package com.iloveplan.android.asis.db;

import java.sql.Timestamp;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.iloveplan.android.asis.MainApp;
import com.iloveplan.android.asis.util.DateUtil;

public class PlanDateDAO {

    private static PlanDateDAO instance;

    private PlanDateDAO() {

    }

    public static PlanDateDAO getInstance() {
        if (instance == null)
            instance = new PlanDateDAO();
        return instance;
    }

    public long insert(PlanDateDVO dvo) {

        // 값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("PLAN_NO", dvo.getPlanNo());
        values.put("PLAN_DT", dvo.getPlanDt());
        values.put("SUCCESS_YN", dvo.getSuccessYn());
        values.put("MEMO_TXT", dvo.getMemoTxt());

        // 테이블에 반영합니다.
        return MainApp.sqlite.insert("TB_PLAN_DATE", null, values);
    }

    public int update(PlanDateDVO dvo) {

        // 값을 설정합니다.
        ContentValues values = new ContentValues();
        values.put("SUCCESS_YN", dvo.getSuccessYn());
        values.put("MEMO_TXT", dvo.getMemoTxt());
        values.put("CHG_TS", DateUtil.getCurrentTime());

        // 테이블에 반영합니다.
        return MainApp.sqlite.update("TB_PLAN_DATE", values, "PLAN_NO = ? AND PLAN_DT = ?", new String[] { String.valueOf(dvo.getPlanNo()), dvo.getPlanDt() });
    }

    public PlanDateDVO select(int planNo, String planDt) {
        PlanDateDVO dvo = null;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN_DATE WHERE PLAN_NO = ? AND PLAN_DT = ?", new String[] { String.valueOf(planNo), planDt });
        if (cursor.moveToNext()) {
            dvo = new PlanDateDVO();
            dvo.setPlanNo(cursor.getInt(cursor.getColumnIndex("PLAN_NO")));
            dvo.setPlanDt(cursor.getString(cursor.getColumnIndex("PLAN_DT")));
            dvo.setSuccessYn(cursor.getString(cursor.getColumnIndex("SUCCESS_YN")));
            dvo.setMemoTxt(cursor.getString(cursor.getColumnIndex("MEMO_TXT")));
            dvo.setRegTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("REG_TS"))));
            dvo.setChgTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("CHG_TS"))));
        }
        cursor.close();
        return dvo;
    }

    /**
     * 최대일자를 반환합니다.
     */
    public String selectMaxPlanDt(int planNo) {
        String planDt = null;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT MAX(PLAN_DT) FROM TB_PLAN_DATE WHERE PLAN_NO = ?", new String[] { String.valueOf(planNo) });
        if (cursor.moveToNext())
            planDt = cursor.getString(0);
        cursor.close();
        return planDt;
    }

    /**
     * 성공건수를 반환합니다.
     */
    public int selectSuccessCount(int planNo) {
        int count = 0;
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT COUNT(1) FROM TB_PLAN_DATE WHERE PLAN_NO = ? AND PLAN_DT <= ? AND SUCCESS_YN = 'Y'", new String[] { String.valueOf(planNo), DateUtil.getCurrentTime("yyyyMMdd") });
        if (cursor.moveToNext())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public ArrayList<PlanDateDVO> selectList(int planNo) {

        // 목록을 정의합니다.
        ArrayList<PlanDateDVO> list = new ArrayList<PlanDateDVO>();

        // 데이터를 조회합니다.
        Cursor cursor = MainApp.sqlite.rawQuery("SELECT * FROM TB_PLAN_DATE WHERE PLAN_NO = ? ORDER BY PLAN_DT", new String[] { String.valueOf(planNo) });

        // 데이터수만큼 반복합니다.
        while (cursor.moveToNext()) {

            // 데이터를 취득합니다.
            PlanDateDVO dvo = new PlanDateDVO();
            dvo.setPlanNo(cursor.getInt(cursor.getColumnIndex("PLAN_NO")));
            dvo.setPlanDt(cursor.getString(cursor.getColumnIndex("PLAN_DT")));
            dvo.setSuccessYn(cursor.getString(cursor.getColumnIndex("SUCCESS_YN")));
            dvo.setMemoTxt(cursor.getString(cursor.getColumnIndex("MEMO_TXT")));
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

    public int delete(int planNo, String planDt) {
        return MainApp.sqlite.delete("TB_PLAN_DATE", "PLAN_NO = ? AND PLAN_DT = ?", new String[] { String.valueOf(planNo), planDt });
    }

    public int delete(String planNo) {
        return MainApp.sqlite.delete("TB_PLAN_DATE", "PLAN_NO = ?", new String[] { planNo });
    }
}
