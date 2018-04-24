package com.uclaradio.uclaradio.Fragments.StreamingFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleData;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.RadioPlatform;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
  private boolean isPlaying = false;
  private ProgressDialog progressDialog;
  private boolean initialStage = true;
  private ImageView logo, showArtImg;
  private boolean streamLoaded = false;

  private RadioPlatform platform;

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

  // Only updates the current playing show whenever the view is created--obviously will
  //  not update if the show changes while the app is still open.
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    logo = (ImageView) getView().findViewById(R.id.logo);
    streamBtnImg = (ImageButton) getView().findViewById(R.id.stream_btn_img);
    showArtImg = (ImageView) getView().findViewById(R.id.show_art_img);
    final MainActivity mainActivity = (MainActivity) getActivity();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://uclaradio.com/")
            .build();

    platform = retrofit.create(RadioPlatform.class);
    platform.getCurrentShow()
            .enqueue(new Callback<ScheduleData>() {
              @SuppressLint("SetTextI18n") // TODO: Worry about this later
              @Override
              public void onResponse(Call<ScheduleData> call, Response<ScheduleData> response) {
                  if (response.isSuccessful()) {
                    ScheduleData currentShow = response.body();
                    ((TextView) getView().findViewById(R.id.show_title_text))
                            .setText("LIVE: " + currentShow.getTitle());
                    Picasso.get().setLoggingEnabled(true);
                    String imageUrl = "https://uclaradio.com" + currentShow.getPictureUrl();
                    if (currentShow.getPictureUrl() == null)
                      imageUrl = "https://uclaradio.com/img/bear_transparent.png";
                    Picasso.get()
                            .load(imageUrl)
                            .into(showArtImg);
                  } else {
                    Log.e("TAG", "RESPONSE FAILED");
                  }
              }

              @Override
              public void onFailure(Call<ScheduleData> call, Throwable t) {
                Log.e("TAG", "FAILED TO MAKE API CALL");
              }
            });
    streamBtnImg.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mainActivity.getStreamPlayer().playPause();
        if (isPlaying)
          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
        else
          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
        isPlaying = !isPlaying;
      }
    });
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}
