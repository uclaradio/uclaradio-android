package com.uclaradio.uclaradio.Activities;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.widget.ImageView;

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

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;

    int actionBarHeight = 0;
    TypedValue tv = new TypedValue();
    if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
    {
      actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
    }

    actionBar = getSupportActionBar();
    Bitmap background = BitmapFactory.decodeResource(getResources(), R.raw.background);
    BitmapDrawable background_drawable = new BitmapDrawable(Bitmap.createScaledBitmap(background, width * 4, actionBarHeight * 4, false));
    background_drawable.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
    actionBar.setBackgroundDrawable(background_drawable);

    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    actionBar.setCustomView(R.layout.abs_layout);
    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.raw.logo_long);
    BitmapDrawable iconDrawable = new BitmapDrawable(Bitmap.createScaledBitmap(icon, 750, 93, false));
    ImageView logo = findViewById(R.id.logo);
    logo.setImageBitmap(icon);
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
