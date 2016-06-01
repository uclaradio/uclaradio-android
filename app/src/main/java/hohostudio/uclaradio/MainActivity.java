package hohostudio.uclaradio;

import android.app.Activity;
import android.app.DownloadManager;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.*;

public class MainActivity extends AppCompatActivity {
    int listLength = 6;
    Messenger mService = null;
    boolean mIsBound;
    boolean enabled = false;
    boolean playing = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    int lastFetchTime = 0;
    RequestQueue mQueue = null;
    String url = "http://ws.audioscrobbler.com/2.0/?method=user.getRecentTracks&user=uclaradio&api_key=d3e63e89b35e60885c944fe9b7341b76&limit=6&format=json";
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    android.util.Log.v("roger", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONObject("recenttracks").getJSONArray("track");
                        android.util.Log.v("roger", "Array length is" + array.length());
                        SongInfo[] arr = new SongInfo[listLength];
                        for(int i = 0; i < listLength; i++) {
                            JSONObject cur = array.getJSONObject(i);
                            String artist = cur.getJSONObject("artist").getString("#text");
                            String name = cur.getString("name");
                            android.util.Log.v("roger", "Artist is: " + artist + " and songName is " + name);
                            SongInfo curSong = new SongInfo(artist, name, "");
                            if(i < listLength) {
                                arr[i] = curSong;
                            }
                        }
                        mAdapter = new RecentlyPlayedAdapter(arr);
                        mRecyclerView.setAdapter(mAdapter);

                    } catch (Exception e) {
                        android.util.Log.v("roger", "json exception: " + e);
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.v("roger", error.toString());
        }
    });

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            View loader = findViewById(R.id.loader);
            View btn = findViewById(R.id.playButton);
            switch (msg.what) {

                case MediaPlayerService.MSG_SET_INT_VALUE:
                    break;
                case MediaPlayerService.MSG_SET_STRING_VALUE:
                    break;
                case MediaPlayerService.MSG_PLAYER_PREPARED:
                    loader.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                    enabled = true;
                    break;
                case MediaPlayerService.MSG_PLAYER_BUFFER_START:
                    android.util.Log.v("roger", "buffering");
                    enabled = false;
                    loader.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.INVISIBLE);

                    break;
                case MediaPlayerService.MSG_PLAYER_BUFFER_END:
                    android.util.Log.v("roger", "buffering end");
                    enabled = true;
                    loader.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
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
        mRecyclerView = (RecyclerView) findViewById(R.id.recentList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(mQueue == null) {
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mQueue = new RequestQueue(cache, network);
            mQueue.start();
        }

        SongInfo[] arr = new SongInfo[1];
        arr[0] = new SongInfo("ArtistName", "SongName","");
        mAdapter = new RecentlyPlayedAdapter(arr);
        mRecyclerView.setAdapter(mAdapter);


        doBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.v("roger", "in resume");
        int curTime = (int) (System.currentTimeMillis());
        if(curTime - lastFetchTime < 120000) {
            android.util.Log.v("roger", "fetched recently, returning");
            return;
        }
        lastFetchTime = curTime;
        mQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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