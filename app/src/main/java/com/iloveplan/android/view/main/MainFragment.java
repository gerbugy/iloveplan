package com.iloveplan.android.view.main;

import android.view.View;

import com.iloveplan.android.view.BaseFragment;

public abstract class MainFragment extends BaseFragment {

    private boolean mHasFloatingActionButton;

    public void onFloatingActionButtonClick(View v) {

    }

    protected void setHasFloatingActionButton(boolean hasFloatingActionButton) {
        mHasFloatingActionButton = hasFloatingActionButton;
    }

    public boolean hasFloatingActionButton() {
        return mHasFloatingActionButton;
    }
}
