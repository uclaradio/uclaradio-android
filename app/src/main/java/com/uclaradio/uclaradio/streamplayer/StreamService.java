package com.uclaradio.uclaradio.streamplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.Activities.PreSplashActivity;
import com.uclaradio.uclaradio.Activities.SplashActivity;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleData;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.RadioPlatform;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.uclaradio.uclaradio.Activities.MainActivity.CHANNEL_ID;

public class StreamService extends Service implements MediaPlayer.OnPreparedListener {
    private final static String STREAM_URL = "http://uclaradio.com:8000/;";
    private final static String BROADCAST_ACTION = "BROADCAST_COMPLETE";

    private Bitmap showArt;
    private String showArtUrl = "https://uclaradio.com/img/bear_transparent.png";
    private String showTitle  = "Loading show...";

    final Target[] notificationTarget = new Target[1];

    private MediaPlayer stream;
    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        showArt = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        initStream();
        registerReceiver(toggleReceiver, new IntentFilter("com.uclaradio.uclaradio.togglePlayPause"));
        checkCurrentTime();
        Log.d("Service", "Started!");

        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Service", "Prepared!");
        Intent prepared = new Intent(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(prepared);
    }

    @Override
    public void onDestroy() {
        stream.stop();
        stream.release();
        unregisterReceiver(toggleReceiver);
        Log.d("Service", "Destroyed");
        super.onDestroy();
    }

    public void play() { stream.start(); }

    public void pause() { stream.pause(); }

    public void toggle() {
        if (isPlaying()) pause();
        else play();
    }

    public boolean isPlaying() { return stream.isPlaying(); }

    private void initStream() {
        stream = new MediaPlayer();
        stream.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            stream.setDataSource(STREAM_URL);
            Log.d("MediaPlayer", "Set data source");
        } catch (IOException ex) {
            Log.e("MediaPlayerErr", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Log.e("MediaPlayerErr", ex.getMessage());
        }
        stream.setOnPreparedListener(this);
        stream.prepareAsync();
        Log.d("Service", "Preparing...");
    }

    private void checkCurrentTime() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int currMin = calendar.get(Calendar.MINUTE);
                if (currMin % 60 < 3)
                    updateCurrentShowInfo();
            }
        }, 0, 20000); // Run every 15 seconds
    }

    public void updateCurrentShowInfo() {
        Log.d("Test", "Updating");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://uclaradio.com/")
                .build();

        RadioPlatform platform = retrofit.create(RadioPlatform.class);
        platform.getCurrentShow()
                .enqueue(new Callback<ScheduleData>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(retrofit2.Call<ScheduleData> call, Response<ScheduleData> response) {
                        if (response.isSuccessful()) {
                            ScheduleData currentShow = response.body();
                            if (currentShow.getTitle() == null)
                                showTitle = "No show playing.";
                            else
                                showTitle = "LIVE: " + currentShow.getTitle();
                            if (currentShow.getPictureUrl() != null)
                                showArtUrl = "https://uclaradio.com" + currentShow.getPictureUrl();

                            Intent intent = new Intent("UpdateShowInfo");
                            intent.putExtra("showTitle", showTitle);
                            intent.putExtra("showArtUrl", showArtUrl);
                            sendBroadcast(intent);

                            final NotificationManager manager =
                                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationTarget[0] = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    setShowArt(bitmap);
                                    manager.notify(MainActivity.SERVICE_ID,
                                            setUpNotification(getApplicationContext(), showTitle, bitmap));
                                    Log.d("Test", "Bitmap loaded!");
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    Log.e("Test", "Bitmap load failed...");
                                    Picasso.get()
                                            .load(showArtUrl)
                                            .into(this);
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            };
                            Picasso.get().setLoggingEnabled(true);
                            Picasso.get()
                                    .load(showArtUrl)
                                    .into(notificationTarget[0]);
                            Log.d("Test", "Updated show info!");
                        } else {
                            Log.e("TAG", "RESPONSE FAILED");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ScheduleData> call, Throwable t) {
                        Log.e("Error", "FAILED TO MAKE API CALL");
                        updateCurrentShowInfo();
                    }
                });

        Log.d("Test", "No update");
    }

    public Notification setUpNotification(Context context, String newTitle, Bitmap newArt) {
        Intent applicationIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            applicationIntent = new Intent(context, PreSplashActivity.class);
        else
            applicationIntent = new Intent(context, SplashActivity.class);
        applicationIntent.setAction(Intent.ACTION_MAIN);
        applicationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent applicationPendingIntent = PendingIntent.getActivity(context, 0, applicationIntent, 0);

        Intent playPauseIntent = new Intent("com.uclaradio.uclaradio.togglePlayPause");
        int playPauseDrawable;
        if (stream != null && isPlaying()) playPauseDrawable = android.R.drawable.ic_media_pause;
        else playPauseDrawable = android.R.drawable.ic_media_play;
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 1, playPauseIntent, 0);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // TODO: Change this to a better icon
                .setContentTitle(newTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(applicationPendingIntent)
                .addAction(playPauseDrawable, "Play/Pause", playPausePendingIntent)
                .setLargeIcon(newArt)
                .setShowWhen(false)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0));

        createNotificationChannel();
        return notifBuilder.build();
    }

    public Notification setUpNotification(Context context) {
        return setUpNotification(context, showTitle, showArt);
    }

    // For API 26 and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "UCLA Radio Stream";
            String description = "Persistent notification for managing stream.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private BroadcastReceiver toggleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toggle();
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(MainActivity.SERVICE_ID, setUpNotification(context));
        }
    };

    // Accessors and mutators

    public void setShowArt(Bitmap bitmap) { showArt = bitmap; }

    public String getShowArtUrl() { return showArtUrl; }
    public String getShowTitle() { return showTitle; }

    public class LocalBinder extends Binder {
        public StreamService getService() {
            return StreamService.this;
        }
    }
}
