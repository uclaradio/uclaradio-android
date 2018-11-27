package com.uclaradio.uclaradio.activities.tabpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

import com.uclaradio.uclaradio.fragments.about.AboutFragment;
import com.uclaradio.uclaradio.fragments.djs.DJsFragment;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleFragment;
import com.uclaradio.uclaradio.fragments.streaming.StreamingFragment;
import com.uclaradio.uclaradio.R;

public class TabPager extends FragmentPagerAdapter {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private Context context;

    public TabPager(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new StreamingFragment();
            case 1:
                return new ScheduleFragment();
            case 2:
                return new DJsFragment();
            case 3:
                return new AboutFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.stream_h);
            case 1:
                return context.getString(R.string.shows_h);
            case 2:
                return context.getString(R.string.djs_h);
            case 3:
                return context.getString(R.string.about_h);
            default:
                return null;
        }
    }

}