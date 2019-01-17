package com.uclaradio.uclaradio.activities;

import java.util.ArrayList;
import java.lang.Math;
import java.net.URISyntaxException;

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
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;

import com.squareup.picasso.Picasso;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.activities.tabpager.TabPager;
import com.uclaradio.uclaradio.fragments.about.AboutFragment;
import com.uclaradio.uclaradio.fragments.djs.DJsFragment;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleFragment;
import com.uclaradio.uclaradio.fragments.streaming.StreamingFragment;
import com.uclaradio.uclaradio.stream.StreamService;
import com.uclaradio.uclaradio.chat.ChatMessage;
import com.uclaradio.uclaradio.chat.MessageListAdapter;

public class MainActivity extends AppCompatActivity
        implements StreamingFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        DJsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

  private static int SERVICE_ID;

  public static StreamService stream;
  private boolean bound = false;

  private View chatBottomSheet;
  private RecyclerView chatRecycler;
  private MessageListAdapter chatAdapter;

  private ArrayList<ChatMessage> messages;
  private String chatUsername;

  private Socket radioSocket;

  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SERVICE_ID = getResources().getInteger(R.integer.service_id);

    messages = new ArrayList<>();
    messages.add(new ChatMessage(0, "Guest121", "first message", "09:00:12"));
    messages.add(new ChatMessage(1, "Guest102", "sup", "09:10:44"));
    messages.add(new ChatMessage(2, "Guest123", "i am out of message ideas", "10:04:00"));
    messages.add(new ChatMessage(3, "Guest177", "Hello world!", "11:05:23"));
    messages.add(new ChatMessage(4, "named-user", "yo this is my jam", "11:49:12"));
    messages.add(new ChatMessage(5, "Guest177", "I love that song!!", "11:50:11"));
    messages.add(new ChatMessage(6, "Guest115", "generic spam message", "12:39:22"));
    messages.add(new ChatMessage(7, "Guest121", "show now?", "01:00:12"));
    messages.add(new ChatMessage(8, "OnAirDiscJockey", "yes i have show now", "01:05:42"));

    context = this;

    socketConnect();

    initializeChat(messages);

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
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    unbindService(connection);
    bound = false;
    stream.stopForeground(true);
    stream.stopSelf();
    socketDisconnect();
    super.onDestroy();
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }

  private void socketConnect() {
    try { radioSocket = IO.socket(getString(R.string.website)); }
    catch (URISyntaxException ex) { ex.printStackTrace(); }

    // listener to grab site-generated username (i.e. GuestXXX)
    radioSocket.on("assign username", onAddUser);
    radioSocket.connect();
    radioSocket.emit("add user"); // add the user to the site's server
  }

  private void socketDisconnect() {
    radioSocket.disconnect();
  }

  private void setUsername(String username) {
    chatUsername = username;
    Log.d("DEBUGGING", chatUsername);
  }

  private void initializeChat(ArrayList<ChatMessage> messages) {
    chatBottomSheet = findViewById(R.id.chat_bottomsheet);
    chatRecycler = (RecyclerView) findViewById(R.id.chat_messages);
    chatAdapter = new MessageListAdapter(this, messages);

    LinearLayoutManager manager = new LinearLayoutManager(this);

    chatRecycler.setLayoutManager(manager);
    chatRecycler.setAdapter(chatAdapter);
    chatRecycler.scrollToPosition(messages.size() - 1);

    BottomSheetBehavior chatBehavior = BottomSheetBehavior.from(chatBottomSheet);
    // final View dimOverlay = findViewById(R.id.dim_overlay);
    final View tabContainer = findViewById(R.id.tab_container);
    final ImageView chatIcon = findViewById(R.id.chat_icon);
    chatBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
        //  dimOverlay.setVisibility(View.GONE);
          chatIcon.setImageResource(R.drawable.chat_icon);
        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
          chatIcon.setImageResource(R.drawable.baseline_keyboard_arrow_down_white_24);
        }
      }

      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        // dimOverlay.setVisibility(View.VISIBLE);
        // dimOverlay.setAlpha((float) Math.sqrt(slideOffset));
        tabContainer.setAlpha(1-slideOffset); 
      }
    });
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
      stream.startForeground
              (SERVICE_ID, stream.setUpNotification(MainActivity.this, true));
      stream.updateCurrentShowInfo();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
    }
  };

  private Emitter.Listener onAddUser = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      ((MainActivity) context).runOnUiThread(new Runnable() {
        @Override
        public void run() {
          String username = (String) args[0];
          setUsername(username);
        }
      });
    }
  };
}
