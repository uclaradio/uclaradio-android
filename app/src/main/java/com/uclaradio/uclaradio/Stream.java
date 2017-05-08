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

public class Stream extends AppCompatActivity {

    private Button streamBtn;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        streamBtn = (Button) findViewById(R.id.streamBtn);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
        streamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playPause){
                    streamBtn.setText("Pause Streaming");

                    if (initialStage) {
                        new Player().execute("http://ssaurel.com/tmp/mymusic.mp3");
                    } else {
                        if(!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }
                    playPause = true;
                } else {
                    streamBtn.setText("Launch Streaming");

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
                        streamBtn.setText("Launch Streaming");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();
                prepared = true;
            }catch(Exception e) {
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

            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }
}
