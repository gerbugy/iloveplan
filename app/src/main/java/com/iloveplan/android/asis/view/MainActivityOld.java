package com.iloveplan.android.asis.view;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iloveplan.android.R;
import com.iloveplan.android.asis.view.plan.PlanListFragment;
import com.iloveplan.android.view.main.MainActivity;

public final class MainActivityOld extends FragmentActivity {

    /**
     * 탭
     */
    private static final int TAB_INDEX_PLAN = 0;
    private static final int TAB_INDEX_COUNT = 1;

    /**
     * 뷰페이져
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private int mViewPagerScrollState;

    /**
     * 백버튼클릭수
     */
    private int mBackPressedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 레이아웃을 설정합니다.
        setContentView(R.layout.main_old);

        // 액션바를 설정합니다.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        // 뷰페이져어댑터를 정의합니다.
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // 뷰페이져를 설정합니다.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mBackPressedCount = 0;
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mViewPagerScrollState = state;
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        closeContextMenu();
                        // closeOptionsMenu();
                    case ViewPager.SCROLL_STATE_IDLE:
                        invalidateOptionsMenu();
                        break;
                }
                super.onPageScrollStateChanged(state);
            }
        });

        // 액션바에 탭을 추가합니다.
        for (int i = 0; i < mViewPagerAdapter.getCount(); i++)
            actionBar.addTab(actionBar.newTab().setText(mViewPagerAdapter.getPageTitle(i)).setTabListener(mTabListener));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // 탭스크롤중에는 홈메뉴버튼을 숨깁니다.(깜빡현상방지)
        menu.setGroupVisible(R.id.hardware_menu, mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE);

        // 탭스크롤중에는 추가버튼는 비활성화합니다.
        MenuItem addMenuItem = menu.findItem(R.id.add);
        if (addMenuItem != null)
            addMenuItem.setEnabled(mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedCount++ < 1) {
            Toast.makeText(this, R.string.main_exit_confirm, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private final class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case TAB_INDEX_PLAN:
                    return new PlanListFragment();
            }
            throw new IllegalStateException("No fragment at i " + i);
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_INDEX_PLAN:
                    return getString(R.string.title_tab1);
            }
            throw new IllegalStateException("No pageTitle at position " + position);
        }
    }

    private TabListener mTabListener = new TabListener() {

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }
    };
}
