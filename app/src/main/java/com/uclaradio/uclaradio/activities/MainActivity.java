package com.uclaradio.uclaradio.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.activities.tabpager.TabPager;
import com.uclaradio.uclaradio.fragments.about.AboutFragment;
import com.uclaradio.uclaradio.fragments.djs.DJsFragment;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleFragment;
import com.uclaradio.uclaradio.fragments.streaming.StreamingFragment;
import com.uclaradio.uclaradio.stream.StreamService;

public class MainActivity extends AppCompatActivity
        implements StreamingFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        DJsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

  private static int SERVICE_ID;

  public static StreamService stream;
  private boolean bound = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SERVICE_ID = getResources().getInteger(R.integer.service_id);

    initializeActionBar();
  }

  @Override
  protected void onStart() {
    super.onStart();

    Intent intent = new Intent(this, StreamService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    Log.d("Service", "Stopped");
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    unbindService(connection);
    bound = false;
    stream.stopForeground(true);
    stream.stopSelf();
    Log.d("Service", "Destroying stream...");
    super.onDestroy();
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }

  private void initializeActionBar() {
    android.support.v7.app.ActionBar actionBar = getSupportActionBar();
    int color = getResources().getColor(R.color.actionBarBackground);
    if (actionBar != null) {
      actionBar.setBackgroundDrawable(new ColorDrawable(color));
      actionBar.setElevation(0);
      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
      actionBar.setCustomView(R.layout.abs_layout);
      actionBar.setDisplayShowTitleEnabled(true);
    }
    ImageView logo = findViewById(R.id.logo);
    try { // If there isn't enough memory to load the bitmap on the UI thread, use Picasso to make it async
      Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_banner_white);
      logo.setImageBitmap(icon);
    } catch (OutOfMemoryError ex) {
      Picasso.get()
              .load(R.drawable.logo_banner_white)
              .into(logo);
    }


    ViewPager viewPager = findViewById(R.id.viewpager);
    viewPager.setAdapter(new TabPager(this, getSupportFragmentManager()));
    viewPager.setOffscreenPageLimit(3);

    TabLayout tabLayout = findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(viewPager);
  }

  public boolean isBound() { return bound; }

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      StreamService.LocalBinder binder = (StreamService.LocalBinder) iBinder;
      stream = binder.getService();
      bound = true;
      if (stream == null) Log.d("Service", "It looks like the stream is null, but...");
      Log.d("Service", binder.getService().toString());
      Log.d("Service", "Bound.");
      stream.startForeground
              (SERVICE_ID, stream.setUpNotification(MainActivity.this, true));
      stream.updateCurrentShowInfo();
      Log.d("Service", "Started in foreground.");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
        Log.d("Service", "Disconnected.");
    }
  };
}
