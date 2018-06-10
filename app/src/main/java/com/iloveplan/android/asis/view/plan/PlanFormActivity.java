package com.iloveplan.android.asis.view.plan;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.util.AppWidgetUtil;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.StringUtil;
import com.iloveplan.android.asis.Settings;

public class PlanFormActivity extends Activity {

    private TextView mTvPlanNo;
    private EditText mEtPlanNm;
    private TextView mTvPlanStdt;
    private TextView mTvPlanEddt;
    private Button mBtnSave;
    private LinearLayout mLayOnWeekDay;
    private CheckBox mChkONMonYn;
    private CheckBox mChkOnTueYn;
    private CheckBox mChkOnWedYn;
    private CheckBox mChkOnThuYn;
    private CheckBox mChkOnFriYn;
    private CheckBox mChkOnSatYn;
    private CheckBox mChkOnSunYn;
    private CheckBox mChkOnHolidayYn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_form);

        // 전달객체를 취득합니다.
        final Intent intent = getIntent();

        // 액션바를 설정합니다.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowHomeEnabled(false);

        // 계획번호입니다.
        mTvPlanNo = (TextView) findViewById(R.id.tvPlanNo);
        mTvPlanNo.setText(intent.getStringExtra("PLAN_NO"));

        // 계획명입니다.
        mEtPlanNm = (EditText) findViewById(R.id.etPlanNm);
        mEtPlanNm.addTextChangedListener(mPlanNmWatcher);

        // 시작일자입니다.
        mTvPlanStdt = (TextView) findViewById(R.id.tvPlanStdt);

        // 종료일자입니다.
        mTvPlanEddt = (TextView) findViewById(R.id.tvPlanEddt);
        mTvPlanEddt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 키패드를 숨깁니다.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                // 팝업메뉴를 보입니다.
                PopupMenu popup = new PopupMenu(PlanFormActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.plan_period, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (R.id.choice == item.getItemId()) {
                            showDatePicker();
                        } else {
                            calcPlanEddt(item.getItemId());
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        // 요일반복입니다.
        mLayOnWeekDay = (LinearLayout) findViewById(R.id.layOnWeekDay);
        mChkONMonYn = (CheckBox) findViewById(R.id.chkOnMonYn);
        mChkOnTueYn = (CheckBox) findViewById(R.id.chkOnTueYn);
        mChkOnWedYn = (CheckBox) findViewById(R.id.chkOnWedYn);
        mChkOnThuYn = (CheckBox) findViewById(R.id.chkOnThuYn);
        mChkOnFriYn = (CheckBox) findViewById(R.id.chkOnFriYn);
        mChkOnSatYn = (CheckBox) findViewById(R.id.chkOnSatYn);
        mChkOnSunYn = (CheckBox) findViewById(R.id.chkOnSunYn);
        mChkONMonYn.setOnTouchListener(mDayTouchListener);
        mChkOnTueYn.setOnTouchListener(mDayTouchListener);
        mChkOnWedYn.setOnTouchListener(mDayTouchListener);
        mChkOnThuYn.setOnTouchListener(mDayTouchListener);
        mChkOnFriYn.setOnTouchListener(mDayTouchListener);
        mChkOnSatYn.setOnTouchListener(mDayTouchListener);
        mChkOnSunYn.setOnTouchListener(mDayTouchListener);

        // 공휴일포함여부입니다.
        mChkOnHolidayYn = (CheckBox) findViewById(R.id.chkOnHolidayYn);
        mChkOnHolidayYn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChkOnHolidayYn.setText(getResources().getString(isChecked ? R.string.plan_on_holiday_yes : R.string.plan_on_holiday_no));
            }
        });

        // 저장버튼입니다.
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnSave.setEnabled(false);
        mBtnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        // 취소버튼입니다.
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // 신규이면
        if (StringUtil.isEmpty(mTvPlanNo.getText().toString())) {
            mTvPlanStdt.setText(DateUtil.getCurrentTime(getString(R.string.plan_date_format)));
            calcPlanEddt(R.id.month1);
        }
        // 수정이면
        else {
            actionBar.setTitle(R.string.plan_edit);
            selectData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 종료일자 선택리스너입니다.
     */
    private class DateSetListener implements OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DATE, dayOfMonth);
            String selectedDt = DateUtil.format(cal.getTime(), "yyyyMMdd");
            String currentDt = DateUtil.getCurrentTime("yyyyMMdd");
            if (selectedDt.compareTo(currentDt) < 0)
                selectedDt = currentDt;
            mTvPlanEddt.setText(DateUtil.format(selectedDt, getString(R.string.plan_date_format)));
        }
    }

    private void showDatePicker() {

        // 캘린더객체를 초기화합니다.
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parse(mTvPlanEddt.getText().toString(), getString(R.string.plan_date_format)));

        // 날짜선택창을 취득합니다.
        DatePickerDialog dpd = new DatePickerDialog(PlanFormActivity.this, new DateSetListener(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        // 날짜선택창을 띄웁니다.
        dpd.setCanceledOnTouchOutside(true);
        dpd.show();
    }

    private TextWatcher mPlanNmWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnSave.setEnabled(s.toString().trim().length() > 0);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private OnTouchListener mDayTouchListener = new OnTouchListener() {

        private int mDownIndex;
        private int mPrevIndex;
        private int mMoveSign;
        private boolean mChecked;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownIndex = getIndex(event.getRawX());
                mPrevIndex = mDownIndex;
                mMoveSign = 0;
                break;
            case MotionEvent.ACTION_MOVE:

                // 인덱스를 취득합니다.
                int currIndex = getIndex(event.getRawX());

                // 인덱스가 유효하면
                if (currIndex > -1 && mPrevIndex != currIndex) {

                    // 이동방향을 인식합니다.
                    if (mMoveSign == 0) {
                        mMoveSign = currIndex - mPrevIndex;
                    } else if (mMoveSign != currIndex - mPrevIndex) {
                        mMoveSign = currIndex - mPrevIndex;
                        mDownIndex = mPrevIndex;
                    }

                    // 최초건을 처리합니다.
                    if (mPrevIndex == mDownIndex) {
                        CheckBox chkDown = (CheckBox) mLayOnWeekDay.getChildAt(mDownIndex);
                        chkDown.setChecked(!chkDown.isChecked());
                        mChecked = chkDown.isChecked();
                    }

                    // 현재건을 처리합니다.
                    CheckBox chkCurr = ((CheckBox) mLayOnWeekDay.getChildAt(currIndex));
                    chkCurr.setChecked(mChecked);

                    // 인덱스를 보관합니다.
                    mPrevIndex = currIndex;
                }
                break;
            }
            return false;
        }

        private int getIndex(float x) {
            for (int i = 0; i < mLayOnWeekDay.getChildCount(); i++) {
                View v = mLayOnWeekDay.getChildAt(i);
                if (v.getLeft() <= x && x <= v.getRight())
                    return i;
            }
            return -1;
        }
    };

    // TODO: 스트링을 반환하는걸로...
    private void calcPlanEddt(int itemId) {
        String planStdt = DateUtil.format(mTvPlanStdt.getText().toString(), getString(R.string.plan_date_format), "yyyyMMdd");
        String planEddt = null;

        switch (itemId) {
        case R.id.month1:
            planEddt = DateUtil.addDate(DateUtil.addMonth(planStdt, 1), -1);
            break;
        case R.id.month2:
            planEddt = DateUtil.addDate(DateUtil.addMonth(planStdt, 2), -1);
            break;
        case R.id.year1:
            planEddt = DateUtil.addDate(DateUtil.addYear(planStdt, 1), -1);
            break;
        }
        if (planEddt != null) {
            mTvPlanEddt.setText(DateUtil.format(planEddt, getString(R.string.plan_date_format)));
        }
    }

    private void saveData() {

        // 분기처리합니다.
        if (StringUtil.isEmpty(mTvPlanNo.getText().toString())) {
            this.insertData();
        } else {
            this.updateData();
        }

        // 앱위젯을 업데이트합니다.
        AppWidgetUtil.update(this);

        // 종료합니다.
        finish();
    }

    private void insertData() {

        // 등록값을 설정합니다.
        PlanDVO dvo = new PlanDVO();
        dvo.setPlanNm(mEtPlanNm.getText().toString());
        dvo.setPlanStdt(DateUtil.format(mTvPlanStdt.getText().toString(), getString(R.string.plan_date_format), "yyyyMMdd"));
        dvo.setPlanEddt(DateUtil.format(mTvPlanEddt.getText().toString(), getString(R.string.plan_date_format), "yyyyMMdd"));
        dvo.setOnMonYn(mChkONMonYn.isChecked() ? "Y" : "N");
        dvo.setOnTueYn(mChkOnTueYn.isChecked() ? "Y" : "N");
        dvo.setOnWedYn(mChkOnWedYn.isChecked() ? "Y" : "N");
        dvo.setOnThuYn(mChkOnThuYn.isChecked() ? "Y" : "N");
        dvo.setOnFriYn(mChkOnFriYn.isChecked() ? "Y" : "N");
        dvo.setOnSatYn(mChkOnSatYn.isChecked() ? "Y" : "N");
        dvo.setOnSunYn(mChkOnSunYn.isChecked() ? "Y" : "N");
        dvo.setOnHolidayYn(mChkOnHolidayYn.isChecked() ? "Y" : "N");

        // 등록합니다.
        long sequence = PlanDAO.getInstance().insert(dvo);

        // 결과값을 설정합니다.
        Intent resultData = getIntent();
        resultData.putExtra("PLAN_NO", String.valueOf(sequence));
        setResult(Settings.RESULT_INSERTED, resultData);
    }

    private void updateData() {

        // 변경값을 설정합니다.
        PlanDVO dvo = new PlanDVO();
        dvo.setPlanNo(Integer.parseInt(mTvPlanNo.getText().toString()));
        dvo.setPlanNm(mEtPlanNm.getText().toString());
        dvo.setPlanStdt(DateUtil.format(mTvPlanStdt.getText().toString(), getString(R.string.plan_date_format), "yyyyMMdd"));
        dvo.setPlanEddt(DateUtil.format(mTvPlanEddt.getText().toString(), getString(R.string.plan_date_format), "yyyyMMdd"));
        dvo.setOnMonYn(mChkONMonYn.isChecked() ? "Y" : "N");
        dvo.setOnTueYn(mChkOnTueYn.isChecked() ? "Y" : "N");
        dvo.setOnWedYn(mChkOnWedYn.isChecked() ? "Y" : "N");
        dvo.setOnThuYn(mChkOnThuYn.isChecked() ? "Y" : "N");
        dvo.setOnFriYn(mChkOnFriYn.isChecked() ? "Y" : "N");
        dvo.setOnSatYn(mChkOnSatYn.isChecked() ? "Y" : "N");
        dvo.setOnSunYn(mChkOnSunYn.isChecked() ? "Y" : "N");
        dvo.setOnHolidayYn(mChkOnHolidayYn.isChecked() ? "Y" : "N");

        // 변경합니다.
        PlanDAO.getInstance().update(dvo);

        // 결과값을 설정합니다.
        Intent resultData = getIntent();
        resultData.putExtra("PLAN_NO", mTvPlanNo.getText().toString());
        setResult(Settings.RESULT_UPDATED, resultData);
    }

    private void selectData() {

        // 조회합니다.
        PlanDVO dvo = PlanDAO.getInstance().select(mTvPlanNo.getText().toString());

        // 출력합니다.
        mEtPlanNm.setText(dvo.getPlanNm());
        mTvPlanStdt.setText(DateUtil.format(dvo.getPlanStdt(), getString(R.string.plan_date_format)));
        mTvPlanEddt.setText(DateUtil.format(dvo.getPlanEddt(), getString(R.string.plan_date_format)));
        mChkONMonYn.setChecked("Y".equals(dvo.getOnMonYn()));
        mChkOnTueYn.setChecked("Y".equals(dvo.getOnTueYn()));
        mChkOnWedYn.setChecked("Y".equals(dvo.getOnWedYn()));
        mChkOnThuYn.setChecked("Y".equals(dvo.getOnThuYn()));
        mChkOnFriYn.setChecked("Y".equals(dvo.getOnFriYn()));
        mChkOnSatYn.setChecked("Y".equals(dvo.getOnSatYn()));
        mChkOnSunYn.setChecked("Y".equals(dvo.getOnSunYn()));
        mChkOnHolidayYn.setChecked("Y".equals(dvo.getOnHolidayYn()));
    }
}
