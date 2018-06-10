package com.iloveplan.android.asis.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.iloveplan.android.asis.db.VersionDVO;
import com.iloveplan.android.asis.util.AppUtil;
import com.iloveplan.android.asis.util.HttpUtil;

public final class VersionCheckAsyncTask extends AsyncTask<Void, Void, VersionDVO> {

    private Context mContext;

    public VersionCheckAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        if (!AppUtil.canConnectNetwork(mContext, false))
            cancel(true);
    }

    @Override
    protected VersionDVO doInBackground(Void... params) {
        try {
            return HttpUtil.request("/version", VersionDVO.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(VersionDVO versionDVO) {

        // 결과값을 체크합니다.
        if (versionDVO == null)
            return;

        // 최소버전이면 구글플레이로 이동합니다.
        if (AppUtil.getApplicationVersionCode(mContext) <= versionDVO.getApplicationVersionMin()) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
            return;
        }

        // 달력을 업데이트합니다.
        new CalendarUpdateAsyncTask(mContext, versionDVO.getCalendarVersion()).execute();
    }
}
