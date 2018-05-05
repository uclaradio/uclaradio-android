package com.uclaradio.uclaradio.Fragments.StreamingFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleData;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.RadioPlatform;
import com.uclaradio.uclaradio.streamplayer.StreamService;

import org.w3c.dom.Text;

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
  private ImageView showArtImg;
  private final Intent callIntent = new Intent(Intent.ACTION_CALL);
  private Button onAirCallBtn, requestCallBtn;

  private StreamService stream;

  private static final int REQUEST_PHONE_CALL = 982;

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

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[],
                                         @NonNull int grantResults[]) {
      switch (requestCode) {
        case REQUEST_PHONE_CALL:
          if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
              startActivity(callIntent);
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
              if (!shouldShowRequestPermissionRationale(permissions[0]))
              {
                new AlertDialog.Builder(getContext())
                        .setTitle("Calling disabled")
                        .setMessage("It looks like you've permanently disabled calling from this app. " +
                                "To re-enable calling, navigate to Settings > Apps > UCLA Radio > Permissions and "
                                + "tap the \"Phone\" option.")
                        .setPositiveButton("OK", null)
                        .create().show();
              }
            }
          }
          break;
        default:
          // Do nothing
      }
  }

  // Only updates the current playing show whenever the view is created--obviously will
  //  not update if the show changes while the app is still open.
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    streamBtnImg = getView().findViewById(R.id.stream_btn_img);
    showArtImg = getView().findViewById(R.id.show_art_img);
    onAirCallBtn = getView().findViewById(R.id.on_air_btn);
    requestCallBtn = getView().findViewById(R.id.request_call_btn);
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
                    TextView showTitle = getView().findViewById(R.id.show_title_text);
                    if (currentShow.getTitle() == null)
                      showTitle.setText("No show playing.");
                    else
                      showTitle.setText("LIVE: " + currentShow.getTitle());
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
        if (!mainActivity.isBound()) {
          Log.d("Service", "Not yet bound");
          return;
        }
        if (MainActivity.stream == null) {
          Log.d("Service", "Stream is null...");
          return;
        }
        if (MainActivity.stream.isPlaying()) {
            MainActivity.stream.pause();
          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
        }
        else {
          MainActivity.stream.play();
          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
        }
      }
    });


    onAirCallBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          callIntent.setData(Uri.parse("tel:3107949348"));
          if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
          else startActivity(callIntent);
      }
    });

    requestCallBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callIntent.setData(Uri.parse("tel:3108259999"));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
          ActivityCompat.requestPermissions(getActivity(),
                  new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        else startActivity(callIntent);
      }
    });
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}
