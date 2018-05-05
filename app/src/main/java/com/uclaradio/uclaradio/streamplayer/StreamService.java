package com.uclaradio.uclaradio.streamplayer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

public class StreamService extends Service implements MediaPlayer.OnPreparedListener {
    private final String STREAM_URL = "http://uclaradio.com:8000/;";
    private final String BROADCAST_ACTION = "BROADCAST_COMPLETE";

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
        Log.d("Service", "Destroyed");
    }

    public void play() { stream.start(); }

    public void pause() { stream.pause(); }

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

    public class LocalBinder extends Binder {
        public StreamService getService() {
            return StreamService.this;
        }
    }
}
