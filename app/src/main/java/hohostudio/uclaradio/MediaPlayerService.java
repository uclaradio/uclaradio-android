package hohostudio.uclaradio;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener{
    private NotificationManager nm;
    private MediaPlayer mPlayer;
    private WifiManager.WifiLock mWifiLock = null;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_INT_VALUE = 3;
    static final int MSG_SET_STRING_VALUE = 4;
    static final int MSG_PLAYER_PREPARED = 5;
    static final int MSG_PLAYER_PLAY = 6;
    static final int MSG_PLAYER_PAUSE = 7;
    static final int MSG_PLAYER_RESTART = 8;

    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            android.util.Log.v("roger", "msg received" + msg);

            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_PLAYER_RESTART:
                    mPlayer.release();
                    mPlayer = null;
                    preparePlayer();
                    break;
                case MSG_PLAYER_PLAY:
                    android.util.Log.v("roger", "playing player");
                    mPlayer.start();
                    break;
                case MSG_PLAYER_PAUSE:
                    android.util.Log.v("roger", "pausing player");
                    mPlayer.pause();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void onPrepared(MediaPlayer player) {
        android.util.Log.v("roger", "prepared");
        //player.start();
        sendMessageToUI(MSG_PLAYER_PREPARED);
    }

    public boolean onInfo(MediaPlayer player, int what, int extra) {
        //if 701 then display loading, if 702 then display play again
        android.util.Log.v("roger", "onInfo " + what);
        android.util.Log.v("roger", "and extra is: " + extra);
        switch(what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                break;
            default:

        }
        return true;
    }

    public boolean onError(MediaPlayer player, int what, int extra) {
        //if 1, 1004...server is busy do something about it
        if(what == 1 && extra == -1004) {
            android.util.Log.v("roger", "server error");
        }
        return true;
    }

    private void sendMessageToUI(int intvaluetosend) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null, intvaluetosend));
                // Send data as an Integer
                //mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));

                //Send data as a String
                /*
                Bundle b = new Bundle();
                b.putString("str1", "ab" + intvaluetosend + "cd");
                Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                msg.setData(b);
                mClients.get(i).send(msg);
                */

            }
            catch (Exception e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("roger", "Service Started.");
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        mWifiLock.acquire();

        preparePlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("roger", "Received start id " + startId + ": " + intent);
        return START_STICKY; // run until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.v("roger", "destroying service");
        //teardown mplayer
        nm.cancel(R.string.service_started); // Cancel the persistent notification.
        mPlayer.release();
        mPlayer = null;

        mWifiLock.release();
    }

    @Override
    protected void finalize() {
        try {
            android.util.Log.v("roger", "releasing mediaplayer in finalzie");
            mPlayer.release();
        } catch(Exception e) {
            android.util.Log.v("roger", "error in finalize: " + e.toString());
        }
    }

    private void preparePlayer() {
        if(mPlayer != null) {
            return;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        try {
            mPlayer.setDataSource("http://stream.uclaradio.com:8000/listen");
            mPlayer.prepareAsync();
        } catch (Exception e) {
            android.util.Log.v("roger", e.toString());
        }
    }

}