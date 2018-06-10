package com.iloveplan.android.asis.view.plan;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.db.CalendarDAO;
import com.iloveplan.android.asis.db.CalendarDVO;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.db.PlanDateDAO;
import com.iloveplan.android.asis.db.PlanDateDVO;
import com.iloveplan.android.asis.util.AppWidgetUtil;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;
import com.iloveplan.android.asis.Settings;

public class PlanViewActivity extends Activity {

    private boolean mRefreshDataRequired = true;
    private TextView mTvPlanNo;
    public PlanDVO mPlanDVO;
    public ArrayList<PlanDateDVO> mPlanDateList;

    // 달력
    private PlanWeekFragment mMonthFragment;

    // 일별상세
    private TextView mTvPlanDt;
    private TextView mTvPlanDtLunar;
    private TextView mTvMemoTxt;

    // 진동
    private Vibrator mVibrator;

    // 설정변경리스너입니다.
    private SharedPreferences mSharedPreferences;
    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Settings.PREF_PLAN_BATCHED_TIME)) {
                mRefreshDataRequired = true;
                selectData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_view);

        // 전달객체를 취득합니다.
        final Intent intent = getIntent();

        // 액션바를 설정합니다.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowHomeEnabled(false);

        // 설정변경리스너를 등록합니다.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        // 계획번호입니다.
        mTvPlanNo = (TextView) findViewById(R.id.tvPlanNo);
        mTvPlanNo.setText(intent.getStringExtra("PLAN_NO"));

        // 프래그먼트를 설정합니다.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mMonthFragment = new PlanWeekFragment(System.currentTimeMillis());
        ft.replace(R.id.frmCalendar, mMonthFragment);
        ft.commit();

        // 일자정보입니다.
        mTvPlanDt = (TextView) findViewById(R.id.tvPlanDt);
        mTvPlanDtLunar = (TextView) findViewById(R.id.tvPlanDtLunar);

        // 메모내용입니다.
        mTvMemoTxt = (TextView) findViewById(R.id.tvMemoTxt);
        mTvMemoTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PlanViewDateFormActivity.class);
                intent.putExtra("PLAN_NO", mTvPlanNo.getText().toString());
                intent.putExtra("PLAN_DT", mTvPlanDt.getTag().toString());
                startActivityForResult(intent, Settings.REQUEST_EDIT);
            }
        });
        mTvMemoTxt.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        // 일별상세는 세로모드에서만 보입니다.
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT)
            ((LinearLayout) findViewById(R.id.llDateMemo)).setVisibility(View.GONE);

        // 진동객체를 취득합니다.
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plan_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.today:
            Time time = new Time();
            time.setToNow();
            mMonthFragment.goTo(time.toMillis(false), false, true, true);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 결과코드를 체크합니다.
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        // 복원메소드(onResume)를 통해 데이터를 재조회합니다.
        mRefreshDataRequired = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    private void selectData() {

        // 조회가 필요하면
        if (mRefreshDataRequired) {

            // 계획을 조회합니다.
            mPlanDVO = PlanDAO.getInstance().select(mTvPlanNo.getText().toString());
            getActionBar().setTitle(mPlanDVO.getPlanNm());

            // 계획일별을 조회합니다.
            mPlanDateList = PlanDateDAO.getInstance().selectList(mPlanDVO.getPlanNo());

            // 어댑터에 알립니다.
            mMonthFragment.setUpAdapter();

            // 재조회필요여부를 설정합니다.
            mRefreshDataRequired = false;
        }
    }

    public void setSelectedDay(String date) {

        // 양력을 설정합니다.
        mTvPlanDt.setTag(date);
        mTvPlanDt.setText(DateUtil.format(date, getResources().getString(R.string.plan_date_format)));

        // 음력을 설정합니다.
        CalendarDVO calDVO = CalendarDAO.getInstance().select(date);
        mTvPlanDtLunar.setText(calDVO == null ? "" : DateUtil.format(calDVO.getDateLunar(), getResources().getString(R.string.plan_date_lunar_format)));

        // 메모를 설정합니다.
        PlanDateDVO planDateDVO = PlanDateDAO.getInstance().select(Integer.parseInt(mTvPlanNo.getText().toString()), date);
        mTvMemoTxt.setText(planDateDVO == null ? "" : planDateDVO.getMemoTxt());
    }

    public void onDayTapped(String date) {
        if (date.equals(mTvPlanDt.getTag()) && PlanUtil.isOnPlanDay(mPlanDVO, date)) {

            // 실천여부는 일주일내에만 수정할 수 있습니다.
            if (DateUtil.diffOfDate(DateUtil.getCurrentTime("yyyyMMdd"), date) > 6) {
                Toast.makeText(PlanViewActivity.this, R.string.plan_edit_limit, Toast.LENGTH_SHORT).show();
                return;
            }

            // 진동을 울립니다.
            mVibrator.vibrate(10);

            // 계획일별에 반영합니다.
            PlanDateDVO dvo = PlanDateDAO.getInstance().select(Integer.parseInt(mTvPlanNo.getText().toString()), date);
            if (dvo != null) {
                dvo.setSuccessYn("Y".equals(dvo.getSuccessYn()) ? "N" : "Y");
                PlanDateDAO.getInstance().update(dvo);
            } else {
                dvo = new PlanDateDVO();
                dvo.setPlanNo(Integer.parseInt(mTvPlanNo.getText().toString()));
                dvo.setPlanDt(date);
                dvo.setSuccessYn("Y");
                PlanDateDAO.getInstance().insert(dvo);
            }

            // 계획기본에 반영합니다.
            PlanDAO.getInstance().update(mPlanDVO);

            // 앱위젯을 업데이트합니다.
            AppWidgetUtil.update(this);

            // 재조회합니다.
            mRefreshDataRequired = true;
            selectData();
        }
    }
}