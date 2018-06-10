package com.iloveplan.android.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;
import com.iloveplan.android.R;
import com.iloveplan.android.util.AdUtils;
import com.iloveplan.android.asis.widget.FixAppBarLayoutBehavior;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (toolbar != null) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer_content_desc, R.string.close_drawer_content_desc);
                drawerLayout.addDrawerListener(drawerToggle);
                drawerToggle.syncState();
            }
        }
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior()); // 첫번째 클릭이 정상적으로 수행되지 않는 현상을 방지합니다. (참고: https://gist.github.com/chrisbanes/8391b5adb9ee42180893300850ed02f2)
        }
        AdView adView = findViewById(R.id.ad_view);
        if (adView != null) {
            AdUtils.loadAd(adView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdView adView = findViewById(R.id.ad_view);
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        AdView adView = findViewById(R.id.ad_view);
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        AdView adView = findViewById(R.id.ad_view);
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    protected void hideSoftInputFromWindow() {
//        View view = getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        }
//    }

//    protected void setSecure() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); // 최근앱 스크린샷을 방지합니다.
//    }
}
