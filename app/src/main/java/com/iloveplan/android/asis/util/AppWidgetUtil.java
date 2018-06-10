package com.iloveplan.android.asis.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import com.iloveplan.android.asis.widget.ListAppWidgetProvider;

public class AppWidgetUtil {

    public static void update(Context context) {
        new AppWidgetUpdateThread(context).start();
    }

    public static void update(Context context, int appWidgetId) {
        new AppWidgetUpdateThread(context, appWidgetId).start();
    }

    /**
     * 쓰레드입니다.
     */
    private static class AppWidgetUpdateThread extends Thread {

        private Context mContext;
        private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        public AppWidgetUpdateThread(Context context) {
            mContext = context;
        }

        public AppWidgetUpdateThread(Context context, int appWidgetId) {
            mContext = context;
            mAppWidgetId = appWidgetId;
        }

        @Override
        public void run() {

            // 위젯매니저를 취득합니다.
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

            // 위젯아이디를 설정합니다.
            int[] appWidgetIds = null;
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                appWidgetIds = new int[] { mAppWidgetId };
            } else {
                appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, ListAppWidgetProvider.class));
            }

            // 위젯을 업데이트합니다.
            if (appWidgetIds.length > 0) {
                new ListAppWidgetProvider().onUpdate(mContext, appWidgetManager, appWidgetIds);
            }
        }
    }
}
