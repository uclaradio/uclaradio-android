package com.uclaradio.uclaradio.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.streamplayer.StreamService;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(new Intent(this, StreamService.class));
        LocalBroadcastManager.getInstance(this).registerReceiver(streamBroadcastReceiver,
                new IntentFilter("BROADCAST_COMPLETE"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(streamBroadcastReceiver);
        stopService(new Intent(this, StreamService.class));
        super.onDestroy();
    }

    private BroadcastReceiver streamBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    };
}
