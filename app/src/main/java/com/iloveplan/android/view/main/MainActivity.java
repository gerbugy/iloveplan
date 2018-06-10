package com.iloveplan.android.view.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.iloveplan.android.R;
import com.iloveplan.android.databinding.MainBinding;
import com.iloveplan.android.view.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private MainBinding mBinding;
    private MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main);
        mBinding.drawerLayout.addDrawerListener(new MainDrawerListener(this));
        mBinding.navigationView.setNavigationItemSelectedListener(new MainNavigationItemSelectedListener(this));
        mBinding.contentLayout.viewPager.setAdapter(mPagerAdapter = new MainPagerAdapter(this));
        mBinding.contentLayout.viewPager.addOnPageChangeListener(new MainPageChangeListener(this));
        mBinding.contentLayout.tabLayout.setupWithViewPager(mBinding.contentLayout.viewPager);
        mBinding.contentLayout.floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button:
                mPagerAdapter.getItem(mBinding.contentLayout.viewPager.getCurrentItem()).onFloatingActionButtonClick(v);
                break;
        }
    }

    MainPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    MainBinding getBinding() {
        return mBinding;
    }
}