package com.iloveplan.android.asis.view.plan;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.format.Time;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.plan.android.calendar.month.SimpleWeekView;
import com.iloveplan.android.asis.db.CalendarDVO;
import com.iloveplan.android.asis.db.PlanDateDVO;
import com.iloveplan.android.asis.util.CalendarUtil;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;
import com.iloveplan.android.asis.util.StringUtil;

// TODO: 1970년 1월 1일 선택 시 에러발생

public class PlanWeekView extends SimpleWeekView {

    // 색
    private int mMonthNumSaturdayColor;
    private int mMonthNumSundayColor;
    private int mMonthOtherBGColor;
    private Drawable mIcSuccess;
    private Drawable mIcFail;

    // 사이즈
    private static int MONTH_DAY_TEXT_SIZE = 18;
    private static int MONTH_DAY_PADDING_TOP = 3;
    private static int MONTH_DAY_PADDING_RIGHT = 4;
    private static int MONTH_ETC_TEXT_SIZE = 9;
    private static int MONTH_ETC_PADDING_TOP = 15;
    private static int MONTH_ETC_PADDING_RIGHT = 5;
    private static int MONTH_MEMO_PADDING_LEFT = 4;
    private static int MONTH_MEMO_PADDING_BOTTOM = 9;
    private static int MONTH_MEMO_LENGTH = 6;

    // 기타
    private static boolean mInitialized;
    private PlanViewActivity mContext;
    private CalendarUtil mCalendarUtil = CalendarUtil.getInstance();
    private Paint mMonthEtcPaint;
    private Paint mMemoPaint;
    private final int mOrientation;

    public PlanWeekView(Context context) {
        super(context);
        mContext = (PlanViewActivity) context;
        mOrientation = getResources().getConfiguration().orientation;
    }

    @Override
    protected void initView() {
        super.initView();
        if (!mInitialized) {
            if (mScale != 1) {
                MONTH_DAY_TEXT_SIZE *= mScale;
                MONTH_DAY_PADDING_TOP *= mScale;
                MONTH_DAY_PADDING_RIGHT *= mScale;
                MONTH_ETC_TEXT_SIZE *= mScale;
                MONTH_ETC_PADDING_TOP *= mScale;
                MONTH_ETC_PADDING_RIGHT *= mScale;
                MONTH_MEMO_PADDING_LEFT *= mScale;
                MONTH_MEMO_PADDING_BOTTOM *= mScale;
                MONTH_MEMO_LENGTH *= mScale;
            }
            mInitialized = true;
        }
        loadColors(getContext());
        mMonthNumPaint = new Paint();
        mMonthNumPaint.setFakeBoldText(false);
        mMonthNumPaint.setTextSize(MONTH_DAY_TEXT_SIZE);
        mMonthNumPaint.setTextAlign(Align.RIGHT);
        mMonthNumPaint.setTypeface(Typeface.DEFAULT);
        mMonthEtcPaint = new Paint();
        mMonthEtcPaint.setFakeBoldText(false);
        mMonthEtcPaint.setTextSize(MONTH_ETC_TEXT_SIZE);
        mMonthEtcPaint.setTextAlign(Align.RIGHT);
        mMonthEtcPaint.setTypeface(Typeface.DEFAULT);
        mMemoPaint = new Paint();
        mMemoPaint.setStyle(Paint.Style.FILL);
        mMemoPaint.setColor(Color.BLACK);
    }

    private void loadColors(Context context) {
        Resources res = context.getResources();
        mMonthNumSaturdayColor = res.getColor(R.color.week_saturday);
        mMonthNumSundayColor = res.getColor(R.color.week_sunday);
        mMonthOtherBGColor = res.getColor(R.color.month_other_bgcolor);
        mIcSuccess = res.getDrawable(R.drawable.plan_ic_success);
        mIcFail = res.getDrawable(R.drawable.plan_ic_fail);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawGridLines(canvas);
        drawWeekNums(canvas);
        drawSuccessYn(canvas);
        drawDaySeparators(canvas);
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        r.top = 1;
        r.bottom = mHeight - 1;
        p.setColor(mMonthOtherBGColor);
        p.setStyle(Style.FILL);
        for (int i = 0; i < DEFAULT_NUM_DAYS; i++) {
            if (!mFocusDay[i]) {
                r.left = computeDayLeftPosition(i);
                r.right = computeDayLeftPosition(i + 1);
                canvas.drawRect(r, p);
            }
        }
    }

    @Override
    protected void drawWeekNums(Canvas canvas) {

        mMonthNumPaint.setFakeBoldText(false);
        Time time = new Time();
        time.setJulianDay(mFirstJulianDay);
        for (int i = 0; i < DEFAULT_NUM_DAYS; i++) {

            // 날짜를 취득합니다.
            String date = DateUtil.format(time.toMillis(false), "yyyyMMdd");

            // 날짜의 추가정보(음력, 기념일)를 조회합니다.
            CalendarDVO calendarDVO = mCalendarUtil.getCalendarDVO(date);

            // 날짜색을 취득합니다.
            int color = mOtherMonthColor;
            if (mFocusDay[i])
                color = calendarDVO != null && "Y".equals(calendarDVO.getHolidayYn()) ? Color.RED : getMonthNumColor(i);

            // 날짜의 추가정보가 있으면
            if (calendarDVO != null && !StringUtil.isEmpty(calendarDVO.getDateTxt())) {

                // 글자색을 설정합니다.
                mMonthEtcPaint.setColor(color);

                // 좌표값을 취득합니다.
                int x = computeDayLeftPosition(i + 1) - MONTH_ETC_PADDING_RIGHT;
                int y = (int) (-mMonthNumPaint.ascent() + 0.5f) + MONTH_ETC_PADDING_TOP;

                // 세로방향이면
                if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    for (String line : calendarDVO.getDateTxt().split(" ")) {
                        canvas.drawText(line, x, y, mMonthEtcPaint);
                        y += -mMonthEtcPaint.ascent() + mMonthEtcPaint.descent();
                    }
                }
                // 세로방향이 아니면
                else {
                    canvas.drawText(calendarDVO.getDateTxt(), x, y, mMonthEtcPaint);
                }
            }

            // 날짜의 메모여부를 표시합니다.
            PlanDateDVO dateDVO = this.getPlanDateDVO(date);
            if (dateDVO != null && !StringUtil.isEmpty(dateDVO.getMemoTxt())) {
                int x = computeDayLeftPosition(i) + MONTH_MEMO_PADDING_LEFT;
                int y = mHeight - MONTH_MEMO_PADDING_BOTTOM;
                canvas.drawRect(x, y, x + MONTH_MEMO_LENGTH, y + MONTH_MEMO_LENGTH, mMemoPaint);
            }

            // 양력날짜
            int x = computeDayLeftPosition(i + 1) - MONTH_DAY_PADDING_RIGHT;
            int y = (int) (-mMonthNumPaint.ascent() + 0.5f) + MONTH_DAY_PADDING_TOP;
            if (mHasToday && mToday == i)
                mMonthNumPaint.setFakeBoldText(true);
            mMonthNumPaint.setColor(color);
            canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
            if (mHasToday && mToday == i)
                mMonthNumPaint.setFakeBoldText(false);

            time.monthDay++;
        }
    }

    private void drawSuccessYn(Canvas canvas) {

        // 성능향상을 위한 처리입니다.
        Time time = new Time();
        String currentDay = DateUtil.getCurrentTime("yyyyMMdd");
        String firstDay = DateUtil.format(time.setJulianDay(mFirstJulianDay), "yyyyMMdd");
        String lastDay = DateUtil.format(time.setJulianDay(mFirstJulianDay + 6), "yyyyMMdd");
        if (firstDay.compareTo(mContext.mPlanDVO.getPlanEddt()) > 0 || lastDay.compareTo(mContext.mPlanDVO.getPlanStdt()) < 0)
            return;

        // 실천여부를 표시합니다.
        time.setJulianDay(mFirstJulianDay);
        int y = -20;
        for (int i = 0; i < DEFAULT_NUM_DAYS; i++) {

            // 날짜를 취득합니다.
            String date = DateUtil.format(time.toMillis(false), "yyyyMMdd");

            // 날짜가 오늘보다 크면 처리가 불필요합니다.(성능향상)
            if (date.compareTo(currentDay) > 0)
                break;

            // 날짜가 실천대상일이면
            if (PlanUtil.isOnPlanDay(mContext.mPlanDVO, date)) {

                // 좌표값을 취득합니다.
                int x = computeDayLeftPosition(i) + 1;

                // 계획일별정보를 취득합니다.
                PlanDateDVO planDateDVO = this.getPlanDateDVO(date);

                // 실천했으면
                if (planDateDVO != null && "Y".equals(planDateDVO.getSuccessYn())) {
                    mIcSuccess.setBounds(x, y, mIcSuccess.getIntrinsicWidth() + x, mIcSuccess.getIntrinsicHeight() + y);
                    mIcSuccess.draw(canvas);
                }
                // 실천안했으면
                else {
                    mIcFail.setBounds(x, y, mIcFail.getIntrinsicWidth() + x, mIcFail.getIntrinsicHeight() + y);
                    mIcFail.draw(canvas);
                }
            }

            time.monthDay++;
        }
    }

    private PlanDateDVO getPlanDateDVO(String planDt) {
        for (int i = 0; i < mContext.mPlanDateList.size(); i++) {
            PlanDateDVO dvo = mContext.mPlanDateList.get(i);
            if (planDt.equals(dvo.getPlanDt()))
                return dvo;
        }
        return null;
    }

    @Override
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
    }

    private void drawGridLines(Canvas canvas) {
        float lines[] = new float[8 * 4];
        int count = 6 * 4;
        int wkNumOffset = 0;
        int i = 0;
        count += 4;
        lines[i++] = 0;
        lines[i++] = 0;
        lines[i++] = mWidth;
        lines[i++] = 0;
        int y0 = 0;
        int y1 = mHeight;
        while (i < count) {
            int x = computeDayLeftPosition(i / 4 - wkNumOffset);
            lines[i++] = x;
            lines[i++] = y0;
            lines[i++] = x;
            lines[i++] = y1;
        }
        p.setColor(mDaySeparatorColor);
        p.setStrokeWidth(DAY_SEPARATOR_WIDTH);
        canvas.drawLines(lines, 0, count, p);
    }

    private int computeDayLeftPosition(int day) {
        return day * mWidth / 7;
    }

    private int getMonthNumColor(int position) {
        int color = mFocusMonthColor;
        if (position == Time.SATURDAY) {
            color = mMonthNumSaturdayColor;
        } else if (position == Time.SUNDAY) {
            color = mMonthNumSundayColor;
        }
        return color;
    }
}