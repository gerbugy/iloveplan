package com.iloveplan.android.asis.view.plan;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.db.PlanDateDAO;
import com.iloveplan.android.asis.db.PlanDateDVO;
import com.iloveplan.android.asis.util.DateUtil;

public class PlanViewDateFormActivity extends Activity {

    private TextView mTvPlanNo;
    private TextView mTvPlanDt;
    private EditText mEtMemoTxt;
    private Button mBtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_view_date_form);

        // 전달객체를 취득합니다.
        final Intent intent = getIntent();

        // 액션바를 설정합니다.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowHomeEnabled(false);

        // 계획번호입니다.
        mTvPlanNo = (TextView) findViewById(R.id.tvPlanNo);
        mTvPlanNo.setText(intent.getStringExtra("PLAN_NO"));

        // 계획일자입니다.
        mTvPlanDt = (TextView) findViewById(R.id.tvPlanDt);
        mTvPlanDt.setText(intent.getStringExtra("PLAN_DT"));

        // 메모내용입니다.
        mEtMemoTxt = (EditText) findViewById(R.id.etMemoTxt);

        // 취소버튼입니다.
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // 저장버튼입니다.
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveData();
            }
        });

        // 조회합니다.
        selectData();
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

    private void saveData() {

        // 공백을 제거합니다.
        mEtMemoTxt.setText(mEtMemoTxt.getText().toString().trim());

        // 조회합니다.
        PlanDateDVO dvo = PlanDateDAO.getInstance().select(Integer.parseInt(mTvPlanNo.getText().toString()), mTvPlanDt.getText().toString());
        if (dvo != null) {
            dvo.setMemoTxt(mEtMemoTxt.getText().toString());
            PlanDateDAO.getInstance().update(dvo);
        } else {
            dvo = new PlanDateDVO();
            dvo.setPlanNo(Integer.parseInt(mTvPlanNo.getText().toString()));
            dvo.setPlanDt(mTvPlanDt.getText().toString());
            dvo.setMemoTxt(mEtMemoTxt.getText().toString());
            PlanDateDAO.getInstance().insert(dvo);
        }

        // 결과값을 설정합니다.
        // Intent data = getIntent();
        // setResult(RESULT_OK, data);
        setResult(RESULT_OK);

        // 종료합니다.
        finish();
    }

    private void selectData() {

        // 계획일을 취득합니다.
        String planDt = mTvPlanDt.getText().toString();

        // 날짜를 설정합니다.
        getActionBar().setTitle(DateUtil.format(planDt, getResources().getString(R.string.plan_date_form_format)));

        // 계획일별정보를 조회합니다.
        PlanDateDVO dvo = PlanDateDAO.getInstance().select(Integer.parseInt(mTvPlanNo.getText().toString()), mTvPlanDt.getText().toString());
        if (dvo != null) {
            mEtMemoTxt.setText(dvo.getMemoTxt());
        }
    }
}
