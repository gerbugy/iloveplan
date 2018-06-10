package com.iloveplan.android.asis.view.plan;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.Time;

import com.iloveplan.android.asis.view.plan.android.calendar.month.SimpleDayPickerFragment;
import com.iloveplan.android.asis.view.plan.android.calendar.month.SimpleWeeksAdapter;

@SuppressLint("ValidFragment")
public class PlanWeekFragment extends SimpleDayPickerFragment {

    public PlanWeekFragment() {
        this(System.currentTimeMillis());
    }

    public PlanWeekFragment(long initialTime) {
        super(initialTime);
    }

    @Override
    public void onAttach(Activity activity) {
        LIST_TOP_OFFSET = 0;
        super.onAttach(activity);
    }

    @Override
    protected void setUpAdapter() {
        HashMap<String, Integer> weekParams = new HashMap<String, Integer>();
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_NUM_WEEKS, mNumWeeks);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_WEEK_START, mFirstDayOfWeek);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_JULIAN_DAY, Time.getJulianDay(mSelectedDay.toMillis(false), mSelectedDay.gmtoff));
        if (mAdapter == null) {
            mAdapter = new PlanWeekAdapter(getActivity(), weekParams);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }
}
