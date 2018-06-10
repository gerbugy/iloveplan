package com.iloveplan.android.asis.view.plan;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.DraggableListView;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.db.PlanDateDAO;
import com.iloveplan.android.asis.db.PlanDAO.ListType;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;
import com.iloveplan.android.asis.Settings;

public class PlanListOldActivity extends Activity {

    // 멤버변수를 선언합니다.
    private CustomAdapter mListAdapter;
    private DraggableListView mListView;
    private ArrayList<PlanDVO> mListData = new ArrayList<PlanDVO>();

    // 설정변경리스너입니다.
    private SharedPreferences mSharedPreferences;
    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Settings.PREF_PLAN_BATCHED_TIME)) {
                selectData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_draggable);

        // 액션바를 설정합니다.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowHomeEnabled(false);

        // 설정변경리스너를 등록합니다.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        // 리스트어댑터를 설정합니다.
        mListAdapter = new CustomAdapter(this, R.layout.plan_list_old_item, mListData);

        // 리스트뷰를 설정합니다.
        mListView = (DraggableListView) findViewById(android.R.id.list);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // 전달값을 취득합니다.
        final String planNo = item.getIntent().getStringExtra("PLAN_NO");

        // 분기처리합니다.
        switch (item.getItemId()) {
        case Settings.CONTEXT_DELETE:
            new AlertDialog.Builder(this).setTitle(R.string.delete).setMessage(R.string.plan_delete_confirm).setNeutralButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PlanDateDAO.getInstance().delete(planNo);
                    PlanDAO.getInstance().delete(planNo);
                    Toast.makeText(PlanListOldActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                    mListView.clearChoices();
                    selectData();
                }
            }).show();
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

    private void selectData() {

        // 목록을 조회합니다.
        mListData.clear();
        mListData.addAll(PlanDAO.getInstance().selectList(ListType.OLD));

        // 어댑터에 알립니다.
        mListAdapter.notifyDataSetChanged();
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 뷰를 취득합니다.
            if (convertView == null)
                convertView = mInflater.inflate(mResourceLayoutId, parent, false);
            convertView.setTag(position);

            // 값을 취득합니다.
            PlanDVO dvo = mList.get(position);

            // 계획명입니다.
            ((TextView) convertView.findViewById(R.id.tvPlanNm)).setText(dvo.getPlanNm());

            // 계획기간입니다.
            ((TextView) convertView.findViewById(R.id.tvPlanDuration)).setText(DateUtil.format(dvo.getPlanStdt(), "yy.MM.dd") + "~" + DateUtil.format(dvo.getPlanEddt(), "yy.MM.dd"));

            // 실천율입니다.
            int successPercent = PlanUtil.calcSuccessPercent(dvo.getSuccessCount(), dvo.getTotalCount());
            TextView tvSuccessPercent = (TextView) convertView.findViewById(R.id.tvSuccessPercent);
            tvSuccessPercent.setText(successPercent + "%");
            tvSuccessPercent.setTextColor(PlanUtil.getTextColorBySuccessPercent(successPercent));

            // 이벤트를 설정합니다.
            convertView.setOnCreateContextMenuListener(mContextMenuListener);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // 포지션을 취득합니다.
                    int position = Integer.parseInt(v.getTag().toString());

                    // 현재행을 강조합니다.
                    mListView.setItemChecked(position, true);

                    // 상세뷰를 호출합니다.
                    PlanDVO dvo = mList.get(position);
                    Intent intent = new Intent(PlanListOldActivity.this, PlanViewActivity.class);
                    intent.putExtra("PLAN_NO", String.valueOf(dvo.getPlanNo()));
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
                menu.add(Menu.NONE, Settings.CONTEXT_DELETE, Menu.NONE, R.string.delete).setIntent(intent);
            }
        };
    }
}
