package com.iloveplan.android.asis.widget;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.plan.PlanViewActivity;
import com.iloveplan.android.asis.db.PlanDAO;
import com.iloveplan.android.asis.db.PlanDVO;
import com.iloveplan.android.asis.db.PlanDateDAO;
import com.iloveplan.android.asis.db.PlanDateDVO;
import com.iloveplan.android.asis.db.PlanDAO.ListType;
import com.iloveplan.android.asis.util.DateUtil;
import com.iloveplan.android.asis.util.PlanUtil;

public class ListAppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewFactory(getApplicationContext(), intent);
    }

    private class ListViewFactory extends BroadcastReceiver implements RemoteViewsFactory {

        private Context mContext;
        private ArrayList<PlanDVO> mListData;
        private String mToday;

        public ListViewFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {

        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            // 계획정보를 취득합니다.
            PlanDVO planDVO = mListData.get(position);

            // 계획상세정보를 취득합니다.
            PlanDateDVO planDateDVO = PlanDateDAO.getInstance().select(planDVO.getPlanNo(), mToday);

            // 뷰를 생성합니다.
            final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.appwidget_list_item);

            // 계획명입니다.
            views.setTextViewText(R.id.tvPlanNm, planDVO.getPlanNm());

            // 실천율입니다.
            int successPercent = PlanUtil.calcSuccessPercent(planDVO.getSuccessCount(), planDVO.getTotalCount());
            views.setTextViewText(R.id.tvSuccessPercent, successPercent + "%");
            views.setTextColor(R.id.tvSuccessPercent, PlanUtil.getTextColorBySuccessPercent(successPercent));

            // 실천여부입니다.
            views.setImageViewResource(R.id.ivSuccessYn, planDateDVO != null && "Y".equals(planDateDVO.getSuccessYn()) ? R.drawable.plan_ic_success : R.drawable.plan_ic_fail);
            views.setViewVisibility(R.id.ivSuccessYn, PlanUtil.isOnPlanDay(planDVO, mToday) ? View.VISIBLE : View.INVISIBLE);

            // 클릭이벤트를 설정합니다.
            final Intent fillInIntent = getLaunchFillInIntent(planDVO.getPlanNo());
            views.setOnClickFillInIntent(R.id.widget_row, fillInIntent);

            return views;
        }

        @Override
        public void onDataSetChanged() {
            mToday = DateUtil.getCurrentTime("yyyyMMdd");
            mListData = PlanDAO.getInstance().selectList(ListType.NOW);
        }

        private Intent getLaunchFillInIntent(int planNo) {
            final Intent intent = new Intent(mContext, PlanViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("PLAN_NO", String.valueOf(planNo));
            return intent;
        }
    }
}
