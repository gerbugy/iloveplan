package com.iloveplan.android.asis.view.plan.android.calendar.month;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.iloveplan.android.asis.view.plan.android.calendar.CalendarController;
import com.iloveplan.android.asis.view.plan.android.calendar.Utils;

/**
 * 원본 100%
 */
public class SimpleWeeksAdapter extends BaseAdapter implements OnTouchListener {

    private static final String TAG = "MonthByWeek";

    public static final String WEEK_PARAMS_NUM_WEEKS = "num_weeks";
    public static final String WEEK_PARAMS_FOCUS_MONTH = "focus_month";
    public static final String WEEK_PARAMS_SHOW_WEEK = "week_numbers";
    public static final String WEEK_PARAMS_WEEK_START = "week_start";
    public static final String WEEK_PARAMS_JULIAN_DAY = "selected_day";
    public static final String WEEK_PARAMS_DAYS_PER_WEEK = "days_per_week";

    protected static final int WEEK_COUNT = CalendarController.MAX_CALENDAR_WEEK - CalendarController.MIN_CALENDAR_WEEK;
    protected static int DEFAULT_NUM_WEEKS = 6;
    protected static int DEFAULT_MONTH_FOCUS = 0;
    protected static int DEFAULT_DAYS_PER_WEEK = 7;
    protected static int DEFAULT_WEEK_HEIGHT = 32;
    protected static int WEEK_7_OVERHANG_HEIGHT = 7;

    protected static float mScale = 0;
    protected Context mContext;
    protected Time mSelectedDay;
    protected int mSelectedWeek;
    protected int mFirstDayOfWeek;
    protected boolean mShowWeekNumber = false;
    protected GestureDetector mGestureDetector;
    protected int mNumWeeks = DEFAULT_NUM_WEEKS;
    protected int mDaysPerWeek = DEFAULT_DAYS_PER_WEEK;
    protected int mFocusMonth = DEFAULT_MONTH_FOCUS;

    public SimpleWeeksAdapter(Context context, HashMap<String, Integer> params) {
        mContext = context;

        // Get default week start based on locale, subtracting one for use with android Time.
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        mFirstDayOfWeek = cal.getFirstDayOfWeek() - 1;

        if (mScale == 0) {
            mScale = context.getResources().getDisplayMetrics().density;
            if (mScale != 1) {
                WEEK_7_OVERHANG_HEIGHT *= mScale;
            }
        }
        init();
        updateParams(params);
    }

    protected void init() {
        mGestureDetector = new GestureDetector(mContext, new CalendarGestureListener());
        mSelectedDay = new Time();
        mSelectedDay.setToNow();
    }

    public void updateParams(HashMap<String, Integer> params) {
        if (params == null) {
            Log.e(TAG, "WeekParameters are null! Cannot update adapter.");
            return;
        }
        if (params.containsKey(WEEK_PARAMS_FOCUS_MONTH)) {
            mFocusMonth = params.get(WEEK_PARAMS_FOCUS_MONTH);
        }
        if (params.containsKey(WEEK_PARAMS_FOCUS_MONTH)) {
            mNumWeeks = params.get(WEEK_PARAMS_NUM_WEEKS);
        }
        if (params.containsKey(WEEK_PARAMS_SHOW_WEEK)) {
            mShowWeekNumber = params.get(WEEK_PARAMS_SHOW_WEEK) != 0;
        }
        if (params.containsKey(WEEK_PARAMS_WEEK_START)) {
            mFirstDayOfWeek = params.get(WEEK_PARAMS_WEEK_START);
        }
        if (params.containsKey(WEEK_PARAMS_JULIAN_DAY)) {
            int julianDay = params.get(WEEK_PARAMS_JULIAN_DAY);
            mSelectedDay.setJulianDay(julianDay);
            mSelectedWeek = Utils.getWeeksSinceEpochFromJulianDay(julianDay, mFirstDayOfWeek);
        }
        if (params.containsKey(WEEK_PARAMS_DAYS_PER_WEEK)) {
            mDaysPerWeek = params.get(WEEK_PARAMS_DAYS_PER_WEEK);
        }
        refresh();
    }

    public void setSelectedDay(Time selectedTime) {
        mSelectedDay.set(selectedTime);
        long millis = mSelectedDay.normalize(true);
        mSelectedWeek = Utils.getWeeksSinceEpochFromJulianDay(Time.getJulianDay(millis, mSelectedDay.gmtoff), mFirstDayOfWeek);
        notifyDataSetChanged();
    }

    public Time getSelectedDay() {
        return mSelectedDay;
    }

    protected void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return WEEK_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleWeekView v;
        HashMap<String, Integer> drawingParams = null;
        if (convertView != null) {
            v = (SimpleWeekView) convertView;
            drawingParams = (HashMap<String, Integer>) v.getTag();
        } else {
            v = new SimpleWeekView(mContext);
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
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT, (parent.getHeight() - WEEK_7_OVERHANG_HEIGHT) / mNumWeeks);
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

    public void updateFocusMonth(int month) {
        mFocusMonth = month;
        notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            SimpleWeekView view = (SimpleWeekView) v;
            Time day = ((SimpleWeekView) v).getDayFromLocation(event.getX());
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Touched day at Row=" + view.mWeek + " day=" + day.toString());
            }
            if (day != null) {
                onDayTapped(day);
            }
            return true;
        }
        return false;
    }

    protected void onDayTapped(Time day) {
        day.hour = mSelectedDay.hour;
        day.minute = mSelectedDay.minute;
        day.second = mSelectedDay.second;
        setSelectedDay(day);
    }

    protected class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }

    ListView mListView;

    public void setListView(ListView lv) {
        mListView = lv;
    }
}
