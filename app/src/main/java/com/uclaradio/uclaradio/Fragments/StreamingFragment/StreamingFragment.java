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
import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StreamingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StreamingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamingFragment extends Fragment {
  private ImageButton streamBtnImg;
  private boolean playPause;
  private ProgressDialog progressDialog;
  private boolean initialStage = true;
  private ImageView logo;
  private boolean streamLoaded = false;

  private OnFragmentInteractionListener mListener;

  public StreamingFragment() {
  }

  public static StreamingFragment newInstance() {
    StreamingFragment fragment = new StreamingFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_streaming, container, false);
  }

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
    final MainActivity mainActivity = (MainActivity) getActivity();

    streamBtnImg.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mainActivity.getStreamPlayer().playPause();
      }
    });
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}
