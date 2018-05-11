package com.uclaradio.uclaradio.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uclaradio.uclaradio.Fragments.AboutFragment.AboutFragment;
import com.uclaradio.uclaradio.Fragments.DJsFragment.DJsFragment;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleFragment;
import com.uclaradio.uclaradio.Fragments.StreamingFragment.StreamingFragment;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.TabPager.TabPager;
import com.uclaradio.uclaradio.streamplayer.StreamService;

public class MainActivity extends AppCompatActivity
        implements StreamingFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        DJsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

  public static final int SERVICE_ID = 41243;
  public static final String CHANNEL_ID = "41243";

  private ActionBar actionBar;
  public static StreamService stream;
  private boolean bound = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;

    int actionBarHeight = 0;
    TypedValue tv = new TypedValue();
    if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
    }

    actionBar = getSupportActionBar();
    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    BitmapDrawable background_drawable = new BitmapDrawable(Bitmap.createScaledBitmap(background, width * 4, actionBarHeight * 4, false));
    background_drawable.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
//    actionBar.setBackgroundDrawable(background_drawable);
    int color = Color.parseColor("#80333333");
    actionBar.setBackgroundDrawable(new ColorDrawable(color));

    actionBar.setElevation(0);
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    actionBar.setCustomView(R.layout.abs_layout);
    ImageView logo = findViewById(R.id.logo);
    try { // If there isn't enough memory to load the bitmap on the UI thread, use Picasso to make it async
      Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_banner_white);
      logo.setImageBitmap(icon);
    } catch (OutOfMemoryError ex) {
      Picasso.get()
              .load(R.drawable.logo_banner_white)
              .into(logo);
    }

    actionBar.setDisplayShowTitleEnabled(true);

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
      stream.startForeground(SERVICE_ID, stream.setUpNotification(MainActivity.this));
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
