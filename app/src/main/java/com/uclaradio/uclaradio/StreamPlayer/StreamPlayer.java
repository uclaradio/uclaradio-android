package com.uclaradio.uclaradio.StreamPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class StreamPlayer extends AsyncTask<String, Void, Boolean> {
    private MediaPlayer mediaPlayer;
    private boolean initialStage;

    public StreamPlayer(Context context) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        initialStage = true;

        this.execute("http://uclaradio.com:8000/;");
    }

    public void playPause() {
        if(initialStage) {
            Log.d("StreamPlayer", "Stream is loading.");
            return;
        }

        if(mediaPlayer.isPlaying()) {
            Log.d("StreamPlayer", "PAUSE");
            mediaPlayer.pause();
        } else {
            Log.d("StreamPlayer", "PLAY");
            mediaPlayer.start();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean prepared;
        try {
            mediaPlayer.setDataSource(params[0]);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    initialStage = true;
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            });
            mediaPlayer.prepare();
            prepared = true;
        } catch (IllegalArgumentException e) {
            Log.d("IllegarArgument", e.getMessage());
            prepared = false;
            e.printStackTrace();
        } catch (SecurityException e) {
            prepared = false;
            e.printStackTrace();
        } catch (IllegalStateException e) {
            prepared = false;
            e.printStackTrace();
        } catch (IOException e) {
            prepared = false;
            e.printStackTrace();
        }
        return prepared;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        //mediaPlayer.pause();

        initialStage = false;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }
}
