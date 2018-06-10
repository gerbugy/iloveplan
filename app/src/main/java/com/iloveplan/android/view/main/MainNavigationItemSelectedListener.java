package com.iloveplan.android.view.main;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.iloveplan.android.R;

public class MainNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private final MainActivity mActivity;

    MainNavigationItemSelectedListener(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawerLayout = mActivity.getBinding().drawerLayout;
        switch (item.getItemId()) {
            case R.id.app_info:
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                startActivity(intent);
                break;
            default:
                drawerLayout.setTag(item.getItemId());
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void startActivity(Intent intent) {
        mActivity.startActivity(intent);
    }
}