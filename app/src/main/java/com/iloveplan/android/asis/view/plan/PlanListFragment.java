package com.iloveplan.android.asis.view.plan;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.DraggableListView;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.db.PlanDateDAO;
import com.iloveplan.android.asis.db.PlanDateDVO;
import com.iloveplan.android.asis.db.PlanDAO.ListType;
import com.iloveplan.android.asis.util.AppWidgetUtil;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;
import com.iloveplan.android.asis.Settings;

public final class PlanListFragment extends Fragment {

    // 변수를 선언합니다.
    private CustomAdapter mListAdapter;
    private DraggableListView mListView;
    private ArrayList<PlanDVO> mListData = new ArrayList<PlanDVO>();
    private String mToday;
    private Animation mAnimation;
    private Vibrator mVibrator;

    // 설정변경리스너입니다.
    private SharedPreferences mSharedPreferences;
    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // 프래그먼트가 화면에 보이지 않더라도 속성값이 바뀌는 즉시 호출됩니다.
            if (key.equals(Settings.PREF_PLAN_BATCHED_TIME)) {
                selectData();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // 리스트어댑터를 설정합니다.
        mListAdapter = new CustomAdapter(getActivity(), R.layout.plan_list_item, mListData);

        // 설정변경리스너를 등록합니다.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        // 애니메이션을 취득합니다.
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.wave_scale);

        // 진동객체를 취득합니다.
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_draggable, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리스트뷰를 설정합니다.
        mListView = (DraggableListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mListAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setDropListener(mDropListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.plan_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add:
            Intent intent = new Intent(getActivity(), PlanFormActivity.class);
            startActivityForResult(intent, Settings.REQUEST_EDIT);
            break;
        case R.id.old:
            startActivity(new Intent(getActivity(), PlanListOldActivity.class));
            break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case Settings.RESULT_INSERTED:

            // 포커스를 설정합니다.
            mListView.setItemChecked(mListView.getCount(), true);

            // 스크롤바를 설정합니다.
            if (mListView.getCheckedItemPosition() < mListView.getFirstVisiblePosition() || mListView.getCheckedItemPosition() > mListView.getLastVisiblePosition()) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setSelection(mListView.getCheckedItemPosition());
                    }
                });
            }
            break;
        case Settings.RESULT_UPDATED:
            break;
        case Settings.RESULT_DELETED:
            mListView.clearChoices();
            break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // 전달값을 취득합니다.
        final String planNo = item.getIntent().getStringExtra("PLAN_NO");

        // 분기처리합니다.
        switch (item.getItemId()) {
        case Settings.CONTEXT_EDIT:
            Intent intent = new Intent(getActivity(), PlanFormActivity.class);
            intent.putExtra("PLAN_NO", planNo);
            startActivityForResult(intent, Settings.REQUEST_EDIT);
            break;
        case Settings.CONTEXT_DELETE:
            new AlertDialog.Builder(getActivity()).setTitle(R.string.delete).setMessage(R.string.plan_delete_confirm).setNeutralButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PlanDAO.getInstance().delete(planNo);
                    PlanDateDAO.getInstance().delete(planNo);
                    Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                    mListView.clearChoices();

                    // 앱위젯을 업데이트합니다.
                    AppWidgetUtil.update(getActivity());

                    // 목록을 조회합니다.
                    selectData();
                }
            }).show();
            break;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListView != null)
            mListView.setDropListener(null);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    private DraggableListView.DropListener mDropListener = new DraggableListView.DropListener() {
        public void drop(int from, int to) {
            if (from != to) {

                // 행위치를 변경합니다.
                mListData.add(to, mListData.remove(from));
                mListAdapter.notifyDataSetChanged();
                mListView.setItemChecked(to, true);

                // 최하위로 옮기면 옵션버튼때문에 선택행이 보이지 않습니다.
                // 최하위건에 셀렉션을 설정하여 해결하였습니다.
                // 안드로이드의 음악앱도 동일한 현상입니다.
                if (mListView.getCount() - 1 == to) {
                    mListView.setSelection(to);
                }

                // 조회순서를 업데이트합니다.
                ArrayList<PlanDVO> list = new ArrayList<PlanDVO>();
                for (int i = 0; i < mListData.size(); i++) {
                    if (mListData.get(i).getOrderNo() != i + 1) {
                        PlanDVO dvo = mListData.get(i);
                        dvo.setOrderNo(i + 1);
                        list.add(dvo);
                    }
                }

                // 조회순서를 업데이트합니다.
                // 자연스러운 처리를 위하여 쓰레드를 이용합니다.
                new OrderUpdater(list).start();
            }
        }
    };

    private class OrderUpdater extends Thread {

        private ArrayList<PlanDVO> mList;

        public OrderUpdater(ArrayList<PlanDVO> list) {
            mList = list;
        }

        @Override
        public void run() {
            PlanDAO.getInstance().updateOrderNo(mList);
        }
    }

    private class CustomAdapter extends ArrayAdapter<PlanDVO> {

        private LayoutInflater mInflater;
        private int mResourceLayoutId;
        private ArrayList<PlanDVO> mList;

        public CustomAdapter(Context context, int resourceLayoutId, ArrayList<PlanDVO> list) {
            super(context, resourceLayoutId, list);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResourceLayoutId = resourceLayoutId;
            mList = list;
        }

        private void setConvertViewByChecked(View convertView, PlanDVO planDVO, PlanDateDVO planDateDVO) {

            // 계획명입니다.
            // TextView tvPlanNm = (TextView) convertView.findViewById(R.id.tvPlanNm);
            // tvPlanNm.setPaintFlags("Y".equals(planDateDVO.getSuccessYn()) ? tvPlanNm.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : tvPlanNm.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            // tvPlanNm.setTextColor("Y".equals(planDateDVO.getSuccessYn()) ? Color.GRAY : Color.BLACK);

            // 실천율입니다.
            int successPercent = PlanUtil.calcSuccessPercent(planDVO.getSuccessCount(), planDVO.getTotalCount());
            TextView tvSuccessPercent = (TextView) convertView.findViewById(R.id.tvSuccessPercent);
            tvSuccessPercent.setText(successPercent + "%");
            tvSuccessPercent.setTextColor(PlanUtil.getTextColorBySuccessPercent(successPercent));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 뷰를 취득합니다.
            if (convertView == null)
                convertView = mInflater.inflate(mResourceLayoutId, parent, false);
            convertView.setTag(position);

            // 값을 취득합니다.
            PlanDVO planDVO = mList.get(position);
            PlanDateDVO planDateDVO = PlanDateDAO.getInstance().select(planDVO.getPlanNo(), mToday);
            if (planDateDVO == null) {
                planDateDVO = new PlanDateDVO();
                planDateDVO.setPlanNo(planDVO.getPlanNo());
                planDateDVO.setPlanDt(mToday);
            }

            // 계획명입니다.
            TextView tvPlanNm = (TextView) convertView.findViewById(R.id.tvPlanNm);
            tvPlanNm.setText(planDVO.getPlanNm());

            // 계획기간입니다.
            int totalCount = (int) (DateUtil.diffOfDate(planDVO.getPlanEddt(), planDVO.getPlanStdt()) + 1);
            int passCount = (int) (DateUtil.diffOfDate(mToday, planDVO.getPlanStdt()) + 1);
            ((TextView) convertView.findViewById(R.id.tvPlanDuration)).setText(String.format("%d일째", passCount));

            // 계획경과율입니다.
            TextView tvPlanDatePercent = ((TextView) convertView.findViewById(R.id.tvPlanDatePercent));
            tvPlanDatePercent.setText(String.format("(%d일 남음)", totalCount - passCount));
            tvPlanDatePercent.setTextColor(totalCount - passCount <= 3 ? Color.RED : Color.parseColor("#888888"));

            // 뷰를 재설정합니다.
            setConvertViewByChecked(convertView, planDVO, planDateDVO);

            // 성공여부(체크박스)입니다.
            CheckBox cbxSuccessYn = (CheckBox) convertView.findViewById(R.id.cbxSuccessYn);
            cbxSuccessYn.setOnCheckedChangeListener(null); // 초기화 수행
            cbxSuccessYn.setChecked("Y".equals(planDateDVO.getSuccessYn()));
            cbxSuccessYn.setOnCheckedChangeListener(new CbxSuccessYnOnCheckedChangeListener(convertView, planDVO, planDateDVO)); // 값설정 후 수행해야 합니다.
            cbxSuccessYn.setVisibility(PlanUtil.isOnPlanDay(planDVO, mToday) ? View.VISIBLE : View.INVISIBLE);

            // 실천율레이아웃입니다.
            ((LinearLayout) convertView.findViewById(R.id.laySuccess)).setOnClickListener(cbxSuccessYn.getVisibility() == View.VISIBLE ? new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.cbxSuccessYn);
                    checkBox.setChecked(!checkBox.isChecked());
                }
            } : null);

            // 상세조회 이벤트를 설정합니다.
            convertView.setOnCreateContextMenuListener(mContextMenuListener);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // 포지션을 취득합니다.
                    int position = Integer.parseInt(v.getTag().toString());

                    // 현재행을 강조합니다.
                    mListView.setItemChecked(position, true);

                    // 상세뷰를 호출합니다.
                    Intent intent = new Intent(getActivity(), PlanViewActivity.class);
                    intent.putExtra("PLAN_NO", String.valueOf(mList.get(position).getPlanNo()));
                    startActivityForResult(intent, Settings.REQUEST_VIEW);
                }
            });

            // 뷰를 반환합니다.
            return convertView;
        }

        private OnCreateContextMenuListener mContextMenuListener = new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

                // 포지션을 취득합니다.
                int position = Integer.parseInt(v.getTag().toString());

                // 현재행을 강조합니다.
                mListView.setItemChecked(position, true);

                // 현재값을 취득합니다.
                PlanDVO dvo = mList.get(position);

                // 전달값을 설정합니다.
                Intent intent = new Intent();
                intent.putExtra("PLAN_NO", String.valueOf(dvo.getPlanNo()));

                // 메뉴를 생성합니다.
                menu.setHeaderTitle(dvo.getPlanNm());
                menu.add(Menu.NONE, Settings.CONTEXT_EDIT, Menu.NONE, R.string.edit).setIntent(intent);
                menu.add(Menu.NONE, Settings.CONTEXT_DELETE, Menu.NONE, R.string.delete).setIntent(intent);
            }
        };

        private class CbxSuccessYnOnCheckedChangeListener implements OnCheckedChangeListener {

            private View mConvertView;
            private PlanDVO mPlanDVO;
            private PlanDateDVO mPlanDateDVO;

            public CbxSuccessYnOnCheckedChangeListener(View convertView, PlanDVO planDVO, PlanDateDVO planDateDVO) {
                mConvertView = convertView;
                mPlanDVO = planDVO;
                mPlanDateDVO = planDateDVO;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // 진동을 울립니다.
                mVibrator.vibrate(10);

                // 애니메이션을 실행합니다.
                mConvertView.startAnimation(mAnimation);

                // 계획일별에 반영합니다.
                mPlanDateDVO.setSuccessYn(isChecked ? "Y" : "N");
                if (PlanDateDAO.getInstance().update(mPlanDateDVO) == 0) {
                    PlanDateDAO.getInstance().insert(mPlanDateDVO);
                }

                // 계획기본에 반영합니다.
                PlanDAO.getInstance().update(mPlanDVO);

                // 뷰를 재설정합니다.
                setConvertViewByChecked(mConvertView, mPlanDVO, mPlanDateDVO);

                // 앱위젯을 업데이트합니다.
                AppWidgetUtil.update(getContext());
            }
        }
    }

    private void selectData() {

        // 현재일자를 취득합니다.
        mToday = DateUtil.getCurrentTime("yyyyMMdd");

        // 목록을 조회합니다.
        mListData.clear();
        mListData.addAll(PlanDAO.getInstance().selectList(ListType.NOW));

        // 어댑터에 알립니다.
        mListAdapter.notifyDataSetChanged();
    }
}