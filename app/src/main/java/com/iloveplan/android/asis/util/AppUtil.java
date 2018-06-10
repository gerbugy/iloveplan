package com.iloveplan.android.asis.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import com.iloveplan.android.R;

public class AppUtil {

    /**
     * 어플리케이션의 버전코드를 반환합니다.
     */
    public static int getApplicationVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA).versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 네트워크(비행기모드 포함) 연결여부를 반환합니다.
     */
    public static boolean canConnectNetwork(Context context, boolean toasting) {
        if (isAirplaneModeOn(context)) {
            if (toasting)
                Toast.makeText(context, R.string.airplane_mode_fail, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isConnectedOrConnecting(context)) {
            if (toasting)
                Toast.makeText(context, R.string.connect_network_fail, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 네트워크 연결여부를 반환합니다.
     */
    private static boolean isConnectedOrConnecting(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * 비행기모드 여부를 반환합니다.
     */
    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
