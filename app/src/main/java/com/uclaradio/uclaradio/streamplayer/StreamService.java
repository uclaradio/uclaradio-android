package com.uclaradio.uclaradio.streamplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.Activities.PreSplashActivity;
import com.uclaradio.uclaradio.Activities.SplashActivity;
import com.uclaradio.uclaradio.R;

import java.io.IOException;

import static com.uclaradio.uclaradio.Activities.MainActivity.CHANNEL_ID;

public class StreamService extends Service implements MediaPlayer.OnPreparedListener {
    private final static String STREAM_URL = "http://uclaradio.com:8000/;";
    private final static String BROADCAST_ACTION = "BROADCAST_COMPLETE";

    private MediaPlayer stream;
    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        initStream();
        registerReceiver(toggleReceiver, new IntentFilter("com.uclaradio.uclaradio.togglePlayPause"));
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

    public Notification setUpNotification(Context context) {
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
        if (isPlaying()) playPauseDrawable = android.R.drawable.ic_media_pause;
        else playPauseDrawable = android.R.drawable.ic_media_play;
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 1, playPauseIntent, 0);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // TODO: Change this to a better icon
                .setContentTitle("LIVE: [Show title]")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(applicationPendingIntent)
                .addAction(playPauseDrawable, "Play/Pause", playPausePendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setShowWhen(false)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0));
        createNotificationChannel();
        return notifBuilder.build();
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

    public class LocalBinder extends Binder {
        public StreamService getService() {
            return StreamService.this;
        }
    }
}
