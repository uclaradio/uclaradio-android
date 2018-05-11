package com.uclaradio.uclaradio.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.streamplayer.StreamService;

import java.util.ArrayList;
import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private boolean shouldStopService = false;

    // For now, networks that block the stream are hardcoded into a list. In the future, though,
    //  it will be more efficient and flexible to determine the block automatically.
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
        if (manager != null) {
            WifiInfo wifiInfo = manager.getConnectionInfo();

            for (String badWifi : badWifis) {
                Log.d("Test", badWifi);
            }
            Log.d("Test", wifiInfo.getSSID() + " - " + badWifis.contains(wifiInfo.getSSID()));
            if (badWifis.contains(wifiInfo.getSSID())) {
                // Notify user that the connected network won't work
                Log.d("Test", "Connected to wrong network...");
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.wrong_wifi_dialog, wifiInfo.getSSID()))
                        .setPositiveButton(R.string.switch_network, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                shouldStopService = true;
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel_switch_network, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                shouldStopService = true;
                                finish();
                            }
                        }).create().show();
            }
        }

        startService(new Intent(this, StreamService.class));
        LocalBroadcastManager.getInstance(this).registerReceiver(streamBroadcastReceiver,
                new IntentFilter(StreamService.BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(streamBroadcastReceiver);
        if (shouldStopService) stopService(new Intent(this, StreamService.class));
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
