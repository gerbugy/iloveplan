package com.iloveplan.android.asis.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.reflect.TypeToken;
import com.iloveplan.android.asis.MainApp;
import com.iloveplan.android.asis.db.CalendarDAO;
import com.iloveplan.android.asis.db.CalendarDVO;
import com.iloveplan.android.asis.util.AppUtil;
import com.iloveplan.android.asis.util.CalendarUtil;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.Settings;
import com.iloveplan.android.asis.util.HttpUtil;

public class CalendarUpdateAsyncTask extends AsyncTask<Void, Void, ArrayList<CalendarDVO>> {

    private Context mContext;
    private int mLatestCalendarVersion;
    private SharedPreferences mPref;
    private String mStartDt;
    private String mEndDt;

    public CalendarUpdateAsyncTask(Context context, int latestCalendarVersion) {
        mContext = context;
        mLatestCalendarVersion = latestCalendarVersion;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected void onPreExecute() {

        // 최신버전인지 체크합니다.
        if (mPref.getInt(Settings.PREF_CALENDAR_VERSION, -1) == mLatestCalendarVersion) {
            cancel(true);
        }
        // 네트워크를 체크합니다.
        else if (!AppUtil.canConnectNetwork(mContext, false)) {
            cancel(true);
        }
    }

    @Override
    protected ArrayList<CalendarDVO> doInBackground(Void... params) {

        // 기간을 설정합니다.
        String currentDt = DateUtil.getCurrentTime("yyyyMMdd");
        mStartDt = DateUtil.addYear(currentDt, -1).substring(0, 4) + "0101";
        mEndDt = DateUtil.addYear(currentDt, 1).substring(0, 4) + "1231";

        // 조건을 설정합니다.
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("startDt", mStartDt));
        nameValuePairs.add(new BasicNameValuePair("endDt", mEndDt));

        // 달력을 조회합니다.
        try {
            return HttpUtil.request("/calendar", new TypeToken<ArrayList<CalendarDVO>>() {
            }.getType(), nameValuePairs);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<CalendarDVO> calendarList) {

        // 결과값을 체크합니다.
        if (calendarList == null || calendarList.size() == 0)
            return;

        // 성능향상을 위하여 트랜잭션을 이용합니다.
        MainApp.sqlite.beginTransaction();
        try {

            // 캘린더정보를 삭제합니다.
            CalendarDAO.getInstance().delete(mStartDt, mEndDt);

            // 캘린더정보를 등록합니다.
            for (CalendarDVO dvo : calendarList)
                CalendarDAO.getInstance().insert(dvo);

            // 트랜잭션을 커밋합니다.
            MainApp.sqlite.setTransactionSuccessful();

            // 캘린더정보를 초기화합니다.
            // 캘린더정보는 싱글톤패턴을 사용하므로 애플리케이션이 종료된 후에도 일정시간동안 스택에 남아있습니다.
            CalendarUtil.getInstance().clear();

        } finally {
            MainApp.sqlite.endTransaction();
        }

        // 달력버전을 보관합니다.
        mPref.edit().putInt(Settings.PREF_CALENDAR_VERSION, mLatestCalendarVersion).commit();
    }
}
