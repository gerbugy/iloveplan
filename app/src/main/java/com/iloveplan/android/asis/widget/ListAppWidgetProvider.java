package com.iloveplan.android.asis.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.MainActivityOld;
import com.iloveplan.android.asis.view.plan.PlanViewActivity;
import com.iloveplan.android.asis.util.DateUtil;

public class ListAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, ListAppWidgetProvider.class);
            performUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // 위젯수만큼 반복합니다.
        for (int appWidgetId : appWidgetIds) {

            // 리모트뷰입니다.
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_list);
            views.setTextViewText(R.id.tvPlanDt, DateUtil.getCurrentTime(context.getResources().getString(R.string.appwidget_date_format)));
            views.setOnClickPendingIntent(R.id.header, PendingIntent.getActivity(context, 0, new Intent(context, MainActivityOld.class), 0));

            // 목록입니다.
            Intent listIntent = new Intent(context, ListAppWidgetService.class);
            listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(android.R.id.list, listIntent);

            // 목록템플릿입니다.
            PendingIntent pendingIntentTemplate = PendingIntent.getActivity(context, 0, new Intent(context, PlanViewActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(android.R.id.list, pendingIntentTemplate);

            // 업데이트를 요청합니다.
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, android.R.id.list);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
