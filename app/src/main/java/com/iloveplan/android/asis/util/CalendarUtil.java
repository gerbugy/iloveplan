package com.iloveplan.android.asis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.iloveplan.android.asis.db.CalendarDAO;
import com.iloveplan.android.asis.db.CalendarDVO;

public final class CalendarUtil {

    private ArrayList<String> mYearList = new ArrayList<String>();
    private ArrayList<CalendarDVO> mCalendarList = new ArrayList<CalendarDVO>();

    private static CalendarUtil sSingleton;

    private CalendarUtil() {

    }

    public static CalendarUtil getInstance() {
        if (sSingleton == null)
            sSingleton = new CalendarUtil();
        return sSingleton;
    }

    public void clear() {
        mYearList.clear();
        mCalendarList.clear();
    }

    public CalendarDVO getCalendarDVO(String yyyyMMdd) {

        // 조회년도를 취득합니다.
        String yyyy = yyyyMMdd.substring(0, 4);

        // 조회년도가 없으면
        if (!mYearList.contains(yyyy)) {

            // 조회합니다.
            synchronized (this) {
                mCalendarList.addAll(CalendarDAO.getInstance().selectList(yyyy));
            }

            // 정렬합니다.(이진탐색필수조건)
            Collections.sort(mCalendarList, new Comparator<CalendarDVO>() {
                @Override
                public int compare(CalendarDVO dvo1, CalendarDVO dvo2) {
                    return dvo1.getDate().compareTo(dvo2.getDate());
                }
            });

            // 조회년도를 보관합니다.
            mYearList.add(yyyy);
        }

        // 목록에서 검색합니다.
        // 검색속도를 향상시키기 위하여 이진탐색알고리즘을 이용합니다.
        int low = 0;
        int high = mCalendarList.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int compare = mCalendarList.get(mid).getDate().compareTo(yyyyMMdd);
            if (compare < 0) {
                low = mid + 1;
            } else if (compare > 0) {
                high = mid - 1;
            } else {
                return mCalendarList.get(mid);
            }
        }
        return null;
    }
}
