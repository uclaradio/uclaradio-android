package com.uclaradio.uclaradio.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.stream.StreamService;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SplashActivity extends AppCompatActivity {

    private static boolean shouldStopService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d("Test", "Real Splash");

        new CheckConnectionAsync(this)
                .execute(new Server(getString(R.string.website_ip),
                        Integer.parseInt(getString(R.string.stream_port)),
                        getResources().getInteger(R.integer.server_ping_timeout)));

        startService(new Intent(this, StreamService.class));
        LocalBroadcastManager.getInstance(this).registerReceiver(streamBroadcastReceiver,
                new IntentFilter(getString(R.string.broadcast_complete)));
    }

    private static void showSwitchWifiDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(context.getResources().getString(R.string.wrong_wifi_dialog))
                .setPositiveButton(R.string.switch_network, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        shouldStopService = true;
                        ((SplashActivity) context).finish();
                    }
                })
                .setNegativeButton(R.string.cancel_switch_network, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shouldStopService = true;
                        ((SplashActivity) context).finish();
                    }
                }).create().show();
    }

    private class Server {
        String url;
        int port;
        int timeout;

        Server(String url, int port, int timeout) {
            this.url = url;
            this.port = port;
            this.timeout = timeout;
        }
    }

    private static class CheckConnectionAsync extends AsyncTask<Server, Void, Boolean> {

        WeakReference<Context> weakContext;

        CheckConnectionAsync(Context context) {
            weakContext = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Server... servers) {
            Log.d("AsyncTask", "Testing...");
            Server radioServer = servers[0];
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(radioServer.url, radioServer.port), radioServer.timeout);

                Log.d("AsyncTask", "Connecting...");
                return true;
            } catch (Exception ex) {
                // Catching a general Exception is frowned upon, but any Exception means that the app couldn't
                // connect to the server and so needs to be restarted.
                Log.e("AsyncTask", "Error connecting to server.");
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d("AsyncTask", "Connection: " + aBoolean);
            if (!aBoolean) showSwitchWifiDialog(weakContext.get());
        }
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
