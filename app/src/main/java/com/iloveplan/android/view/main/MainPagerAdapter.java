package com.iloveplan.android.view.main;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.iloveplan.android.R;
import com.iloveplan.android.view.habit.HabitListFragment;
import com.iloveplan.android.view.home.HomeFragment;
import com.iloveplan.android.view.memo.MemoListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final MainActivity mActivity;

    private final List<Class<? extends MainFragment>> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    MainPagerAdapter(MainActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
        addFragment(HomeFragment.class, getString(R.string.home));
        addFragment(HabitListFragment.class, getString(R.string.habit));
        addFragment(MemoListFragment.class, getString(R.string.memo));
    }

    @Override
    public MainFragment getItem(int position) {
        for (Fragment fragment : mActivity.getSupportFragmentManager().getFragments()) {
            String tag = fragment.getTag();
            if (tag != null && position == Integer.parseInt(tag.substring(tag.lastIndexOf(":") + 1))) {
                return (MainFragment) fragment;
            }
        }
        try {
            return mFragments.get(position).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    private void addFragment(Class<? extends MainFragment> fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    private String getString(int resId) {
        return mActivity.getString(resId);
    }
}