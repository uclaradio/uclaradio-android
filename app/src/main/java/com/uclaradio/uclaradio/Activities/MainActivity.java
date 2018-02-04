package com.uclaradio.uclaradio.Activities;

import android.app.ActionBar;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uclaradio.uclaradio.Fragments.AboutFragment.AboutFragment;
import com.uclaradio.uclaradio.Fragments.DJsFragment.DJsFragment;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleFragment;
import com.uclaradio.uclaradio.Fragments.StreamingFragment.StreamingFragment;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.TabPager.TabPager;

public class MainActivity extends AppCompatActivity
    implements StreamingFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        DJsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

  private ActionBar actionBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setupTabs();
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }

  private void setupTabs() {
    android.support.v7.app.ActionBar actionBar = getSupportActionBar();

    actionBar.setDisplayShowTitleEnabled(true);

    ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
    viewPager.setAdapter(new TabPager(this, getSupportFragmentManager()));

    TabLayout tabLayout = findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(viewPager);
  }
}
