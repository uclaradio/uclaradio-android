package com.uclaradio.uclaradio.Fragments.StreamingFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
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

import java.util.Observable;

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

  private TextView showTitleTv;
  private ImageView showArtIv;

  private ContentLoadingProgressBar showArtProgress;

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
    getContext().unregisterReceiver(showUpdateReceiver);
    getContext().unregisterReceiver(toggleReceiver);
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
    showArtIv = getView().findViewById(R.id.show_art_img);
    showTitleTv = getView().findViewById(R.id.show_title_text);
    onAirCallBtn = getView().findViewById(R.id.on_air_btn);
    requestCallBtn = getView().findViewById(R.id.request_call_btn);
    showArtProgress = getView().findViewById(R.id.show_art_progress);
    showArtProgress.show();
    final MainActivity mainActivity = (MainActivity) getActivity();

    getContext().registerReceiver(showUpdateReceiver,
            new IntentFilter("UpdateShowInfo"));

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
//        if (MainActivity.stream.isPlaying())
//          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
//        else
//          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
//        MainActivity.stream.toggle();
        getContext().sendBroadcast(new Intent("com.uclaradio.uclaradio.togglePlayPause"));
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

    getContext().registerReceiver(toggleReceiver,
            new IntentFilter("com.uclaradio.uclaradio.togglePlayPause"));
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }

  private BroadcastReceiver showUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        showTitleTv.setText(intent.getStringExtra("showTitle"));
        Picasso.get()
                .load(intent.getStringExtra("showArtUrl"))
                .into(showArtIv, new com.squareup.picasso.Callback() {
                  @Override
                  public void onSuccess() {
                      if (showArtProgress != null) showArtProgress.hide();
                  }

                  @Override
                  public void onError(Exception e) {
                      Log.e("Picasso", "Error in Picasso!");
                      e.printStackTrace();
                  }
                });
    }
  };

  private BroadcastReceiver toggleReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (MainActivity.stream != null && MainActivity.stream.isPlaying())
        streamBtnImg.setImageResource(android.R.drawable.ic_media_pause);
      else
        streamBtnImg.setImageResource(android.R.drawable.ic_media_play);
    }
  };
}
