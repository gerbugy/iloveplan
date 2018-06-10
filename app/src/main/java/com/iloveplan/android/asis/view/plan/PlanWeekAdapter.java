package com.iloveplan.android.asis.view.plan;

import java.util.HashMap;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.iloveplan.android.asis.view.plan.android.calendar.month.SimpleWeekView;
import com.iloveplan.android.asis.view.plan.android.calendar.month.SimpleWeeksAdapter;
import com.iloveplan.android.asis.util.DateUtil;

public class PlanWeekAdapter extends SimpleWeeksAdapter {

    public PlanWeekAdapter(Context context, HashMap<String, Integer> params) {
        super(context, params);
    }

    @Override
    public void setSelectedDay(Time selectedTime) {
        super.setSelectedDay(selectedTime);
        ((PlanViewActivity) mContext).setSelectedDay(DateUtil.format(selectedTime.toMillis(false), "yyyyMMdd"));
    }

    @Override
    protected void onDayTapped(Time day) {
        ((PlanViewActivity) mContext).onDayTapped(DateUtil.format(day.toMillis(false), "yyyyMMdd")); // super.onDayTapped()보다 선수행되어야 합니다.
        super.onDayTapped(day);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlanWeekView v;
        HashMap<String, Integer> drawingParams = null;
        if (convertView != null) {
            v = (PlanWeekView) convertView;
            drawingParams = (HashMap<String, Integer>) v.getTag();
        } else {
            v = new PlanWeekView(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            v.setLayoutParams(params);
            v.setClickable(true);
            v.setOnTouchListener(this);
        }
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Integer>();
        }
        drawingParams.clear();
        int selectedDay = -1;
        if (mSelectedWeek == position) {
            selectedDay = mSelectedDay.weekDay;
        }
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT, (int) ((parent.getHeight() + 2 * mScale) / mNumWeeks));
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SHOW_WK_NUM, mShowWeekNumber ? 1 : 0);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK_START, mFirstDayOfWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_NUM_DAYS, mDaysPerWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK, position);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH, mFocusMonth);
        v.setWeekParams(drawingParams, mSelectedDay.timezone);
        v.invalidate();
        return v;
    }
}
