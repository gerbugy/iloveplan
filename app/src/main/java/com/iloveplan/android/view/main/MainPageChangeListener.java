package com.iloveplan.android.view.main;

import android.support.v4.view.ViewPager;
import android.view.View;

public class MainPageChangeListener implements ViewPager.OnPageChangeListener {

    private final MainActivity mActivity;

    MainPageChangeListener(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) { // 실제 변경된 경우에만 호출됩니다
        boolean hasFloatingActionButton = mActivity.getPagerAdapter().getItem(position).hasFloatingActionButton();
        mActivity.getBinding().contentLayout.floatingActionButton.setVisibility(hasFloatingActionButton ? View.VISIBLE : View.GONE);
        // hideSoftInputFromWindow();
    }
}