package com.iloveplan.android.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.iloveplan.android.R;
import com.iloveplan.android.util.AdUtils;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdView adView = findAdView();
        if (adView != null) {
            AdUtils.loadAd(adView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AdView adView = findAdView();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        AdView adView = findAdView();
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        AdView adView = findAdView();
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private AdView findAdView() {
        View view = getView();
        if (view != null) {
            return view.findViewById(R.id.ad_view);
        }
        return null;
    }
}