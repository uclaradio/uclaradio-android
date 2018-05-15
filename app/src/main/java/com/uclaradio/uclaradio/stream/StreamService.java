package com.uclaradio.uclaradio.stream;

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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.activities.PreSplashActivity;
import com.uclaradio.uclaradio.activities.SplashActivity;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleData;
import com.uclaradio.uclaradio.interfaces.RadioPlatform;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StreamService extends Service implements MediaPlayer.OnPreparedListener {
    private static String STREAM_URL;
    public  static String BROADCAST_ACTION;
    private static String CHANNEL_ID;
    private static int    SERVICE_ID;

    private Bitmap showArt;
    private String showArtUrl;
    private String showTitle;

    private boolean isUserStopped = true;
    private boolean isPreparing = false;

    private boolean startUp = true;

    final Target[] notificationTarget = new Target[1];

    private MediaPlayer stream;
    private final IBinder binder = new LocalBinder();

    private Handler streamStopHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        STREAM_URL = getString(R.string.stream_url);
        BROADCAST_ACTION = getString(R.string.broadcast_complete);
        CHANNEL_ID = getString(R.string.channel_id);
        SERVICE_ID = getResources().getInteger(R.integer.service_id);

        showArt = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        showArtUrl = getString(R.string.website) + "/img/radio.png";
        showTitle = getString(R.string.loading_show);

        streamStopHandler = new Handler();

        initStream();
        registerReceiver(toggleReceiver, new IntentFilter(getString(R.string.play_pause_intent)));
        registerReceiver(connErrReceiver, new IntentFilter(getString(R.string.connection_error)));
        registerReceiver(connRestReceiver, new IntentFilter(getString(R.string.connection_restored)));
        checkCurrentTime();
        Log.d("Service", "Started!");

        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Service", "Prepared!");
        Intent prepared = new Intent(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(prepared);
        if (startUp) toggle();
        toggle(); // If the stream cuts out and reconnects, toggle to reset state
        sendBroadcast(new Intent(getString(R.string.connection_restored)));
        startUp = false;
        isUserStopped = false;
        isPreparing = false;
    }

    @Override
    public void onDestroy() {
        stream.stop();
        stream.release();
        streamStopHandler.removeCallbacks(streamStopper); // Otherwise an IllegalStateException is thrown
        unregisterReceiver(toggleReceiver);
        unregisterReceiver(connErrReceiver);
        unregisterReceiver(connRestReceiver);
        Log.d("Service", "Destroyed");

        super.onDestroy();
    }

    public void play() {
        streamStopHandler.removeCallbacks(streamStopper); // If there's a stop in the queue, remove it
        if (isUserStopped && !isPreparing) {
            // Notify user that stream is reloading
            Log.d("Service", "Stream reloading...");
            sendBroadcast(new Intent(getString(R.string.connection_error)));
            initStream();
        }
        else {
            stream.start();
            Log.d("Service", "Stream started");
        }
    }

    public void pause() {
        stream.pause();
        Log.d("Service", "Stream paused");
        // Queue up a stop for after a given delay
        streamStopHandler.postDelayed(streamStopper,
                getResources().getInteger(R.integer.stream_pause_stop_delay));
    }

    public void toggle() {
        if (isPlaying()) pause();
        else play();
    }

    public boolean isPlaying() {
        try {
            return stream.isPlaying();
        } catch (IllegalStateException ex) {
            Log.e("Service", "Trying to call isPlaying() on an uninitialized MediaPlayer.");
            ex.printStackTrace();
            return isPlaying();
        } catch (NullPointerException ex) {
            Log.e("Service", "Trying to call isPlaying() on a null reference.");
            ex.printStackTrace();
            initStream();
            return isPlaying();
        }
    }

    private void initStream() {
        stream = new MediaPlayer();
        isPreparing = true;
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
        stream.setLooping(false);
        stream.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Using a Toast instead of a Snackbar so that the user knows something's wrong
                // even if they're using a different app. (If the screen is locked or something,
                // the notification play/pause icon will change to an error symbol anyway).
                String connectionDropMessage;
                if (!isUserStopped) // If the stream stopped unexpectedly
                    connectionDropMessage = getString(R.string.couldnt_connect);
                else
                    connectionDropMessage = getString(R.string.reconnecting);

                Toast.makeText(StreamService.this, connectionDropMessage, Toast.LENGTH_LONG)
                            .show();

                Log.d("Service", "Stream stopped. Reconnecting...");
                sendBroadcast(new Intent(getString(R.string.connection_error)));
                stream.reset();
                initStream();
//                sendBroadcast(new Intent(getString(R.string.play_pause_intent)));
            }
        });
        stream.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    // See above
                    Toast.makeText(StreamService.this, R.string.something_wrong, Toast.LENGTH_LONG)
                            .show();
                    Log.d("Service", "Server died. Restarting media player...");
                    stream.reset();
                    initStream();
                    return true;
                }

                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT)
                    Log.d("Service", "Connection timed out. Retrying...");
                else {
                    Log.d("Service", "The media player stopped for some reason. Retrying...");
                    Log.d("Service", what + " " + extra);
                }
                // No need to show an error message here because returning false from onError
                // triggers the onCompletionListener
                return false;
            }
        });
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
        }, 0, 20000); // Run every 20 seconds
    }

    public void updateCurrentShowInfo() {
        Log.d("Test", "Updating");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.website))
                .build();

        RadioPlatform platform = retrofit.create(RadioPlatform.class);
        platform.getCurrentShow()
                .enqueue(new Callback<ScheduleData>() {
                    @Override
                    public void onResponse(retrofit2.Call<ScheduleData> call, Response<ScheduleData> response) {
                        if (response.isSuccessful()) {
                            ScheduleData currentShow = response.body();
                            if (currentShow.getTitle() == null)
                                showTitle = getString(R.string.no_show_playing);
                            else
                                showTitle = "LIVE: " + currentShow.getTitle();
                            if (currentShow.getPictureUrl() != null)
                                showArtUrl = getString(R.string.website) + currentShow.getPictureUrl();
                            else
                                showArtUrl = getString(R.string.website) + "/img/radio.png";

                            Intent intent = new Intent(getString(R.string.update_show_info_intent));
                            intent.putExtra("showTitle", showTitle);
                            intent.putExtra("showArtUrl", showArtUrl);
                            sendBroadcast(intent);

                            final NotificationManager manager =
                                    (NotificationManager) getApplicationContext()
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationTarget[0] = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    setShowArt(bitmap);
                                    if (manager != null)
                                        manager.notify(SERVICE_ID,
                                                setUpNotification(getApplicationContext(), showTitle, bitmap, true));
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
                            Picasso.get()
                                    .load(showArtUrl)
                                    .into(notificationTarget[0]);
                            Log.d("Test", "Updated show info!");
                        } else {
                            Log.e("TAG", "RESPONSE FAILED");
                            updateCurrentShowInfo();
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

    public Notification setUpNotification(Context context, String newTitle, Bitmap newArt, boolean isConnected) {
        Intent applicationIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            applicationIntent = new Intent(context, PreSplashActivity.class);
        else
            applicationIntent = new Intent(context, SplashActivity.class);
        applicationIntent.setAction(Intent.ACTION_MAIN);
        applicationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent applicationPendingIntent = PendingIntent.getActivity(context, 0, applicationIntent, 0);

        Intent playPauseIntent = new Intent(getString(R.string.play_pause_intent));
        int playPauseDrawable;
        if (stream != null && isPlaying()) playPauseDrawable = R.drawable.baseline_pause_white_36;
        else playPauseDrawable = R.drawable.baseline_play_arrow_white_36;

        if (!isConnected) playPauseDrawable = R.drawable.baseline_autorenew_white_36;
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 1, playPauseIntent, 0);

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        int tintColor = Palette.from(newArt).generate().getVibrantColor(value.data);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(newTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(applicationPendingIntent)
                .addAction(playPauseDrawable, "Play/Pause", playPausePendingIntent)
                .setLargeIcon(newArt)
                .setColorized(true)
                .setColor(tintColor)
                .setShowWhen(false)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0));


        createNotificationChannel();
        return notifBuilder.build();
    }

    public Notification setUpNotification(Context context, boolean isConnected) {
        return setUpNotification(context, showTitle, showArt, isConnected);
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
            if (manager != null)
                manager.createNotificationChannel(channel);
        }
    }

    private BroadcastReceiver toggleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toggle();
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.notify(SERVICE_ID, setUpNotification(context, true));
        }
    };

    private BroadcastReceiver connErrReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.notify(SERVICE_ID, setUpNotification(context, false));
        }
    };

    private BroadcastReceiver connRestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.notify(SERVICE_ID, setUpNotification(context, true));
        }
    };

    // Accessors and mutators

    public void setShowArt(Bitmap bitmap) { showArt = bitmap; }

    public String getShowTitle() { return showTitle; }

    private Runnable streamStopper = new Runnable() {
        @Override
        public void run() {
            stream.stop();
            stream.reset();
            isUserStopped = true;
            Log.d("Service", "Stream has stopped.");
        }
    };

    public class LocalBinder extends Binder {
        public StreamService getService() {
            return StreamService.this;
        }
    }
}
