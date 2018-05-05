package com.uclaradio.uclaradio.streamplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class StreamPlayer extends AsyncTask<String, Void, Boolean> {
  private MediaPlayer mediaPlayer;
  private boolean initialStage;
  private ConditionVariable startStream;

  public StreamPlayer(Context context) {
    startStream = new ConditionVariable(false);

    mediaPlayer = new MediaPlayer();
    mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
    initialStage = false;

    this.execute("http://uclaradio.com:8000/;");
  }

  public void playPause() {
    if (initialStage) {
      Log.d("StreamPlayer", "Stream is loading.");
      return;
    }

    if (mediaPlayer.isPlaying()) {
      Log.d("StreamPlayer", "PAUSE");
      mediaPlayer.stop();
      mediaPlayer.reset();
    } else {
      Log.d("StreamPlayer", "PLAY");
      startStream.open();
    }
  }

  @Override
  protected Boolean doInBackground(String... params) {
    Boolean prepared = false;

    while(!isCancelled()) {
      startStream.block();

      initialStage = true;
      try {
        mediaPlayer.setDataSource(params[0]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mp) {
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

      mediaPlayer.start();
      initialStage = false;

      startStream.close();
    }

    return prepared;
  }

  @Override
  protected void onPreExecute() {
    // TODO Auto-generated method stub
    super.onPreExecute();
  }
}
