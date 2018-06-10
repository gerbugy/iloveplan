package com.iloveplan.android.asis.util;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.iloveplan.android.asis.db.CalendarDVO;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.db.PlanDAO.ListType;
import com.iloveplan.android.asis.Settings;

public class PlanUtil {

    public static boolean isOnPlanDay(PlanDVO planDVO, String day) {

        // 계획기간내인지 체크합니다.
        if (day.compareTo(planDVO.getPlanStdt()) < 0 || day.compareTo(planDVO.getPlanEddt()) > 0)
            return false;

        // 미래인지 체크합니다.
        if (DateUtil.getCurrentTime("yyyyMMdd").compareTo(day) < 0)
            return false;

        // 요일반복대상여부를 체크합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(day));
        String onWeekYn = "N";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
            onWeekYn = planDVO.getOnMonYn();
            break;
        case Calendar.TUESDAY:
            onWeekYn = planDVO.getOnTueYn();
            break;
        case Calendar.WEDNESDAY:
            onWeekYn = planDVO.getOnWedYn();
            break;
        case Calendar.THURSDAY:
            onWeekYn = planDVO.getOnThuYn();
            break;
        case Calendar.FRIDAY:
            onWeekYn = planDVO.getOnFriYn();
            break;
        case Calendar.SATURDAY:
            onWeekYn = planDVO.getOnSatYn();
            break;
        case Calendar.SUNDAY:
            onWeekYn = planDVO.getOnSunYn();
            break;
        }
        if ("N".equals(onWeekYn))
            return false;

        // 공휴일대상여부를 체크합니다.
        CalendarDVO calendarDVO = CalendarUtil.getInstance().getCalendarDVO(day);
        if (calendarDVO != null && "Y".equals(calendarDVO.getHolidayYn()))
            if ("N".equals(planDVO.getOnHolidayYn()))
                return false;

        return true;
    }

    /**
     * 오늘까지의 실천대상일수를 계산합니다.
     */
    public static int calcTotalCountUntilToday(PlanDVO planDVO) {
        int count = 0;
        String today = DateUtil.getCurrentTime("yyyyMMdd");
        String planDt;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(planDVO.getPlanStdt()));
        while ((planDt = DateUtil.format(calendar.getTime(), "yyyyMMdd")).compareTo(today) <= 0 && planDt.compareTo(planDVO.getPlanEddt()) <= 0) {
            if (isOnPlanDay(planDVO, planDt))
                count++;
            calendar.add(Calendar.DATE, 1);
        }
        return count;
    }

    /**
     * 실천율에 따른 폰트색을 반환합니다.
     */
    public static int getTextColorBySuccessPercent(int successPercent) {
        if (successPercent >= 70)
            return Color.BLUE;
        else if (successPercent <= 40)
            return Color.RED;
        else
            return Color.BLACK;
    }

    /**
     * 건수정보를 업데이트합니다.
     */
    public static void updateTotalAndSuccessCount(Context context) {

        // 건수정보를 업데이트합니다.
        PlanDAO planDAO = PlanDAO.getInstance();
        for (PlanDVO planDVO : planDAO.selectList(ListType.NOW_WITH_YESTERDAY))
            planDAO.update(planDVO);

        // 앱위젯을 업데이트합니다.
        AppWidgetUtil.update(context);

        // 설정정보를 업데이트합니다.
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Settings.PREF_PLAN_BATCHED_TIME, DateUtil.getCurrentTime()).commit();
    }

    public static int calcSuccessPercent(int successCount, int totalCount) {
        int successPercent = totalCount == 0 ? 0 : (100 * successCount / totalCount);
        return successPercent > 100 ? 100 : successPercent;
    }
}
