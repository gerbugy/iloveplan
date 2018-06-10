package com.iloveplan.android.asis.view.plan.android.calendar.month;

import java.security.InvalidParameterException;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.plan.android.calendar.Utils;

/**
 * 원본 100%
 */
public class SimpleWeekView extends View {

    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_WEEK = "week";
    public static final String VIEW_PARAMS_SELECTED_DAY = "selected_day";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";
    public static final String VIEW_PARAMS_NUM_DAYS = "num_days";
    public static final String VIEW_PARAMS_FOCUS_MONTH = "focus_month";
    public static final String VIEW_PARAMS_SHOW_WK_NUM = "show_wk_num";

    protected static int DEFAULT_HEIGHT = 32;
    protected static int MIN_HEIGHT = 10;
    protected static final int DEFAULT_SELECTED_DAY = -1;
    protected static final int DEFAULT_WEEK_START = Time.SUNDAY;
    protected static final int DEFAULT_NUM_DAYS = 7;
    protected static final int DEFAULT_SHOW_WK_NUM = 0;
    protected static final int DEFAULT_FOCUS_MONTH = -1;

    protected static int DAY_SEPARATOR_WIDTH = 1;

    protected static int MINI_DAY_NUMBER_TEXT_SIZE = 14;
    protected static int MINI_WK_NUMBER_TEXT_SIZE = 12;
    protected static int MINI_TODAY_NUMBER_TEXT_SIZE = 18;
    protected static int MINI_TODAY_OUTLINE_WIDTH = 2;
    protected static int WEEK_NUM_MARGIN_BOTTOM = 4;
    protected static float mScale = 0;
    protected int mPadding = 0;

    protected Rect r = new Rect();
    protected Paint p = new Paint();
    protected Paint mMonthNumPaint;
    protected Drawable mSelectedDayLine;

    protected String[] mDayNumbers;
    protected boolean[] mFocusDay;
    protected boolean[] mOddMonth;
    protected int mFirstJulianDay = -1;
    protected int mFirstMonth = -1;
    protected int mLastMonth = -1;
    protected int mWeek = -1;
    protected int mWidth;
    protected int mHeight = DEFAULT_HEIGHT;
    protected boolean mShowWeekNum = false;
    protected boolean mHasSelectedDay = false;
    protected boolean mHasToday = false;
    protected int mSelectedDay = DEFAULT_SELECTED_DAY;
    protected int mToday = DEFAULT_SELECTED_DAY;
    protected int mWeekStart = DEFAULT_WEEK_START;
    protected int mNumDays = DEFAULT_NUM_DAYS;
    protected int mNumCells = mNumDays;
    protected int mSelectedLeft = -1;
    protected int mSelectedRight = -1;
    protected String mTimeZone = Time.getCurrentTimezone();

    protected int mBGColor;
    protected int mSelectedWeekBGColor;
    protected int mFocusMonthColor;
    protected int mOtherMonthColor;
    protected int mDaySeparatorColor;
    protected int mTodayOutlineColor;
    protected int mWeekNumColor;

    public SimpleWeekView(Context context) {
        super(context);

        Resources res = context.getResources();

        mBGColor = res.getColor(R.color.month_bgcolor);
        mSelectedWeekBGColor = res.getColor(R.color.month_selected_week_bgcolor);
        mFocusMonthColor = res.getColor(R.color.month_mini_day_number);
        mOtherMonthColor = res.getColor(R.color.month_other_month_day_number);
        mDaySeparatorColor = res.getColor(R.color.month_grid_lines);
        mTodayOutlineColor = res.getColor(R.color.mini_month_today_outline_color);
        mWeekNumColor = res.getColor(R.color.month_week_num_color);
        // mSelectedDayLine = res.getDrawable(R.drawable.dayline_minical_holo_light);

        if (mScale == 0) {
            mScale = context.getResources().getDisplayMetrics().density;
            if (mScale != 1) {
                DEFAULT_HEIGHT *= mScale;
                MIN_HEIGHT *= mScale;
                MINI_DAY_NUMBER_TEXT_SIZE *= mScale;
                MINI_TODAY_NUMBER_TEXT_SIZE *= mScale;
                MINI_TODAY_OUTLINE_WIDTH *= mScale;
                WEEK_NUM_MARGIN_BOTTOM *= mScale;
                DAY_SEPARATOR_WIDTH *= mScale;
                MINI_WK_NUMBER_TEXT_SIZE *= mScale;
            }
        }

        initView();
    }

    public void setWeekParams(HashMap<String, Integer> params, String tz) {
        if (!params.containsKey(VIEW_PARAMS_WEEK)) {
            throw new InvalidParameterException("You must specify the week number for this view");
        }
        setTag(params);
        mTimeZone = tz;
        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mHeight < MIN_HEIGHT) {
                mHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_DAY)) {
            mSelectedDay = params.get(VIEW_PARAMS_SELECTED_DAY);
        }
        mHasSelectedDay = mSelectedDay != -1;
        if (params.containsKey(VIEW_PARAMS_NUM_DAYS)) {
            mNumDays = params.get(VIEW_PARAMS_NUM_DAYS);
        }
        if (params.containsKey(VIEW_PARAMS_SHOW_WK_NUM)) {
            if (params.get(VIEW_PARAMS_SHOW_WK_NUM) != 0) {
                mShowWeekNum = true;
            } else {
                mShowWeekNum = false;
            }
        }
        mNumCells = mShowWeekNum ? mNumDays + 1 : mNumDays;

        // Allocate space for caching the day numbers and focus values
        mDayNumbers = new String[mNumCells];
        mFocusDay = new boolean[mNumCells];
        mOddMonth = new boolean[mNumCells];
        mWeek = params.get(VIEW_PARAMS_WEEK);
        int julianMonday = Utils.getJulianMondayFromWeeksSinceEpoch(mWeek);
        Time time = new Time(tz);
        time.setJulianDay(julianMonday);

        // If we're showing the week number calculate it based on Monday
        int i = 0;
        if (mShowWeekNum) {
            mDayNumbers[0] = Integer.toString(time.getWeekNumber());
            i++;
        }

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        }

        // Now adjust our starting day based on the start day of the week
        // If the week is set to start on a Saturday the first week will be
        // Dec 27th 1969 -Jan 2nd, 1970
        if (time.weekDay != mWeekStart) {
            int diff = time.weekDay - mWeekStart;
            if (diff < 0) {
                diff += 7;
            }
            time.monthDay -= diff;
            time.normalize(true);
        }

        mFirstJulianDay = Time.getJulianDay(time.toMillis(true), time.gmtoff);
        mFirstMonth = time.month;

        // Figure out what day today is
        Time today = new Time(tz);
        today.setToNow();
        mHasToday = false;
        mToday = -1;

        int focusMonth = params.containsKey(VIEW_PARAMS_FOCUS_MONTH) ? params.get(VIEW_PARAMS_FOCUS_MONTH) : DEFAULT_FOCUS_MONTH;

        for (; i < mNumCells; i++) {
            if (time.monthDay == 1) {
                mFirstMonth = time.month;
            }
            mOddMonth[i] = (time.month % 2) == 1;
            if (time.month == focusMonth) {
                mFocusDay[i] = true;
            } else {
                mFocusDay[i] = false;
            }
            if (time.year == today.year && time.yearDay == today.yearDay) {
                mHasToday = true;
                mToday = i;
            }
            mDayNumbers[i] = Integer.toString(time.monthDay++);
            time.normalize(true);
        }
        if (time.monthDay == 1) {
            time.monthDay--;
            time.normalize(true);
        }
        mLastMonth = time.month;

        updateSelectionPositions();
    }

    protected void initView() {
        p.setFakeBoldText(false);
        p.setAntiAlias(true);
        p.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        p.setStyle(Style.FILL);
        mMonthNumPaint = new Paint();
        mMonthNumPaint.setFakeBoldText(true);
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setColor(mFocusMonthColor);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
    }

    public int getFirstMonth() {
        return mFirstMonth;
    }

    public int getLastMonth() {
        return mLastMonth;
    }

    public int getFirstJulianDay() {
        return mFirstJulianDay;
    }

    public Time getDayFromLocation(float x) {
        int dayStart = mShowWeekNum ? (mWidth - mPadding * 2) / mNumCells + mPadding : mPadding;
        if (x < dayStart || x > mWidth - mPadding) {
            return null;
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        int dayPosition = (int) ((x - dayStart) * mNumDays / (mWidth - dayStart - mPadding));
        int day = mFirstJulianDay + dayPosition;

        Time time = new Time(mTimeZone);
        if (mWeek == 0) {
            // This week is weird...
            if (day < Time.EPOCH_JULIAN_DAY) {
                day++;
            } else if (day == Time.EPOCH_JULIAN_DAY) {
                time.set(1, 0, 1970);
                time.normalize(true);
                return time;
            }
        }

        time.setJulianDay(day);
        return time;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawWeekNums(canvas);
        drawDaySeparators(canvas);
    }

    protected void drawBackground(Canvas canvas) {
        if (mHasSelectedDay) {
            p.setColor(mSelectedWeekBGColor);
            p.setStyle(Style.FILL);
        } else {
            return;
        }
        r.top = 1;
        r.bottom = mHeight - 1;
        r.left = mPadding;
        r.right = mSelectedLeft;
        canvas.drawRect(r, p);
        r.left = mSelectedRight;
        r.right = mWidth - mPadding;
        canvas.drawRect(r, p);
    }

    protected void drawWeekNums(Canvas canvas) {
        int y = ((mHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2) - DAY_SEPARATOR_WIDTH;
        int nDays = mNumCells;

        int i = 0;
        int divisor = 2 * nDays;
        if (mShowWeekNum) {
            p.setTextSize(MINI_WK_NUMBER_TEXT_SIZE);
            p.setStyle(Style.FILL);
            p.setTextAlign(Align.CENTER);
            p.setAntiAlias(true);
            p.setColor(mWeekNumColor);
            int x = (mWidth - mPadding * 2) / divisor + mPadding;
            canvas.drawText(mDayNumbers[0], x, y, p);
            i++;
        }

        boolean isFocusMonth = mFocusDay[i];
        mMonthNumPaint.setColor(isFocusMonth ? mFocusMonthColor : mOtherMonthColor);
        mMonthNumPaint.setFakeBoldText(false);
        for (; i < nDays; i++) {
            if (mFocusDay[i] != isFocusMonth) {
                isFocusMonth = mFocusDay[i];
                mMonthNumPaint.setColor(isFocusMonth ? mFocusMonthColor : mOtherMonthColor);
            }
            if (mHasToday && mToday == i) {
                mMonthNumPaint.setTextSize(MINI_TODAY_NUMBER_TEXT_SIZE);
                mMonthNumPaint.setFakeBoldText(true);
            }
            int x = (2 * i + 1) * (mWidth - mPadding * 2) / (divisor) + mPadding;
            canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
            if (mHasToday && mToday == i) {
                mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
                mMonthNumPaint.setFakeBoldText(false);
            }
        }
    }

    protected void drawDaySeparators(Canvas canvas) {
        if (mHasSelectedDay) {
            r.top = 1;
            r.bottom = mHeight - 1;
            r.left = mSelectedLeft + 1;
            r.right = mSelectedRight - 1;
            p.setStrokeWidth(MINI_TODAY_OUTLINE_WIDTH);
            p.setStyle(Style.STROKE);
            p.setColor(mTodayOutlineColor);
            canvas.drawRect(r, p);
        }
        if (mShowWeekNum) {
            p.setColor(mDaySeparatorColor);
            p.setStrokeWidth(DAY_SEPARATOR_WIDTH);

            int x = (mWidth - mPadding * 2) / mNumCells + mPadding;
            canvas.drawLine(x, 0, x, mHeight, p);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        updateSelectionPositions();
    }

    protected void updateSelectionPositions() {
        if (mHasSelectedDay) {
            int selectedPosition = mSelectedDay - mWeekStart;
            if (selectedPosition < 0) {
                selectedPosition += 7;
            }
            if (mShowWeekNum) {
                selectedPosition++;
            }
            mSelectedLeft = selectedPosition * (mWidth - mPadding * 2) / mNumCells + mPadding;
            mSelectedRight = (selectedPosition + 1) * (mWidth - mPadding * 2) / mNumCells + mPadding;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Context context = getContext();
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Service.ACCESSIBILITY_SERVICE);
        if (!am.isEnabled() || !am.isTouchExplorationEnabled()) {
            return super.onHoverEvent(event);
        }
        if (event.getAction() != MotionEvent.ACTION_HOVER_EXIT) {
            Time hover = getDayFromLocation(event.getX());
            if (hover != null && (mLastHoverTime == null || Time.compare(hover, mLastHoverTime) != 0)) {
                Long millis = hover.toMillis(true);
                String date = Utils.formatDateRange(context, millis, millis, DateUtils.FORMAT_SHOW_DATE);
                AccessibilityEvent accessEvent = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
                accessEvent.getText().add(date);
                sendAccessibilityEventUnchecked(accessEvent);
                mLastHoverTime = hover;
            }
        }
        return true;
    }

    Time mLastHoverTime = null;
}