package com.uclaradio.uclaradio.Fragments.StreamingFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.Stream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StreamingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StreamingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton streamBtnImg;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private ImageView logo;
    private boolean streamLoaded = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StreamingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StreamingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StreamingFragment newInstance(String param1, String param2) {
        StreamingFragment fragment = new StreamingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_streaming, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        logo = (ImageView) getView().findViewById(R.id.logo);
        streamBtnImg = (ImageButton) getView().findViewById(R.id.streamBtnImg);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(getActivity());

        Picasso.with(getActivity().getApplicationContext()).load("https://raw.githubusercontent.com/uclaradio/uclaradio-iOS/master/UCLA%20Radio/UCLA%20Radio/images/radio_banner%403x.png").into(logo);

        streamBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playPause) {
//                    streamBtn.setText("Pause");
                    streamBtnImg.setImageResource(android.R.drawable.ic_media_pause);

                    if (initialStage) {
                        new StreamingFragment.Player().execute("http://uclaradio.com:8000/;");
                        playPause = true;
                    } else {
                        if(!mediaPlayer.isPlaying() && streamLoaded)
                            mediaPlayer.start();
                            playPause = true;
                    }
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

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                streamLoaded = true;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
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
        }
    }
}
