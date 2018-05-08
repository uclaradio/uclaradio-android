package com.uclaradio.uclaradio.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.data.Globals;
import com.uclaradio.uclaradio.streamplayer.StreamService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    public static ArrayList<String> badWifis
            = new ArrayList<>(Arrays.asList(
            "\"UCLA_WEB\"", "\"UCLA_WEB_RES\""
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d("Test", "Real Splash");

        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();

        for (String badWifi : badWifis) {
            Log.d("Test", badWifi);
        }
        Log.d("Test", wifiInfo.getSSID() + " - " + badWifis.contains(wifiInfo.getSSID()));
        if (badWifis.contains(wifiInfo.getSSID())) {
            // Notify user that the connected network won't work
            Log.d("Test", "Connected to wrong network...");
            new AlertDialog.Builder(this)
                    .setMessage("It looks like you're connected to " + wifiInfo.getSSID() +
                            ". The UCLA Radio stream doesn't work due to security settings " +
                            "on this network, so please switch to a different network and " +
                            "restart the app.")
                    .setPositiveButton("SWITCH NETWORKS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            finish();
                        }
                    })
                    .setNegativeButton("I DIDN'T WANT TO TUNE IN ANYWAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        }

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
