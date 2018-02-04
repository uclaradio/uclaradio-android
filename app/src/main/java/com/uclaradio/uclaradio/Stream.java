package com.uclaradio.uclaradio;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Stream extends AppCompatActivity {

//    private Button streamBtn;
  private ImageButton streamBtnImg;
  private boolean playPause;
  private MediaPlayer mediaPlayer;
  private ProgressDialog progressDialog;
  private boolean initialStage = true;
  private ImageView logo;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stream);

    logo = (ImageView) findViewById(R.id.logo);
//        streamBtn = (Button) findViewById(R.id.streamBtn);
    streamBtnImg = (ImageButton) findViewById(R.id.streamBtnImg);
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    progressDialog = new ProgressDialog(this);

    Picasso.with(getApplicationContext()).load("https://raw.githubusercontent.com/uclaradio/uclaradio-iOS/master/UCLA%20Radio/UCLA%20Radio/images/radio_banner%403x.png").into(logo);

    streamBtnImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!playPause) {
//                    streamBtn.setText("Pause");
          streamBtnImg.setImageResource(android.R.drawable.ic_media_pause);

          if (initialStage) {
            new Player().execute("http://uclaradio.com:8000/;");
          } else {
            if(!mediaPlayer.isPlaying())
              mediaPlayer.start();
          }
          playPause = true;
        } else {
//                    streamBtn.setText("Play");
          streamBtnImg.setImageResource(android.R.drawable.ic_media_play);

          if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
          }

          playPause = false;
        }
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (mediaPlayer != null) {
      mediaPlayer.reset();
      mediaPlayer.release();;
      mediaPlayer = null;
    }
  }
  class Player extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... strings) {
      Boolean prepared = false;
      try {
        mediaPlayer.setDataSource(strings[0]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            initialStage = true;
            playPause = false;
//                        streamBtn.setText("Play");//Launch Streaming
            streamBtnImg.setImageResource(android.R.drawable.ic_media_pause);
            mediaPlayer.stop();
            mediaPlayer.reset();
          }
        });

        mediaPlayer.prepare();
        prepared = true;
      } catch(Exception e) {
        Log.e("MyAudoiStreamApp", e.getMessage());
        prepared = false;
      }

      return prepared;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
      super.onPostExecute(aBoolean);

      if (progressDialog.isShowing()) {
        progressDialog.cancel();
      }

      mediaPlayer.start();
      initialStage = false;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

//            progressDialog.setMessage("Buffering..."); //Buffering...
//            progressDialog.show();
    }
  }
}
