package hohostudio.uclaradio;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    Messenger mService = null;
    boolean mIsBound;
    boolean enabled = false;
    boolean playing = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MediaPlayerService.MSG_SET_INT_VALUE:
                    break;
                case MediaPlayerService.MSG_SET_STRING_VALUE:
                    break;
                case MediaPlayerService.MSG_PLAYER_PREPARED:
                    View view = findViewById(R.id.loader);
                    view.setVisibility(View.INVISIBLE);
                    view = findViewById(R.id.playButton);
                    view.setVisibility(View.VISIBLE);
                    enabled = true;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, MediaPlayerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restoreMe(savedInstanceState);

        doBindService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        outState.putString("textStatus", textStatus.getText().toString());
        outState.putString("textIntValue", textIntValue.getText().toString());
        outState.putString("textStrValue", textStrValue.getText().toString());
        */
    }
    private void restoreMe(Bundle state) {
        if (state!=null) {
            /*
            textStatus.setText(state.getString("textStatus"));
            textIntValue.setText(state.getString("textIntValue"));
            textStrValue.setText(state.getString("textStrValue"));
            */
        }
    }

    public void togglePlay(View view) {
        if(!enabled) {
            return;
        }
        ImageButton button = (ImageButton) findViewById(R.id.playButton);
        if(playing) {
            playing = false;
            button.setImageResource(R.drawable.play_button);
            sendMessageToService(MediaPlayerService.MSG_PLAYER_PAUSE);
        } else {
            playing = true;
            button.setImageResource(R.drawable.pause_button);
            sendMessageToService(MediaPlayerService.MSG_PLAYER_PLAY);
        }
    }

    public void fastForward(View view) {
        android.util.Log.v("roger", "fastforward");
        if(!enabled) {
            return;
        }
        View loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        ImageButton playBtn = (ImageButton) findViewById(R.id.playButton);
        playBtn.setVisibility(View.INVISIBLE);
        playBtn.setImageResource(R.drawable.play_button);
        playing = false;
        enabled = false;
        sendMessageToService(MediaPlayerService.MSG_PLAYER_RESTART);
    }

    private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, intvaluetosend, 0, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                }
            }
        }
    }

    void doBindService() {
        bindService(new Intent(this, MediaPlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MediaPlayerService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        }
        catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

}