package com.uclaradio.uclaradio.TabPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

import com.uclaradio.uclaradio.Fragments.AboutFragment.AboutFragment;
import com.uclaradio.uclaradio.Fragments.DJsFragment.DJsFragment;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleFragment;
import com.uclaradio.uclaradio.Fragments.StreamingFragment.StreamingFragment;

public class TabPager extends FragmentPagerAdapter {

    private Context mContext;

    public TabPager(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
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
                return "STREAM";
            case 1:
                return "SHOWS";
            case 2:
                return "DJs";
            case 3:
                return "ABOUT";
            default:
                return null;
        }
    }

}