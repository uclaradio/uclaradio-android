package com.uclaradio.uclaradio.Fragments.StreamingFragment;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.Activities.MainActivity;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.RadioPlatform;
import com.uclaradio.uclaradio.streamplayer.StreamService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StreamingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StreamingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamingFragment extends Fragment {
  private FloatingActionButton playPauseBtn;

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
    playPauseBtn = getView().findViewById(R.id.playpause_btn);
    showArtIv = getView().findViewById(R.id.show_art_img);
    showTitleTv = getView().findViewById(R.id.show_title_text);
    onAirCallBtn = getView().findViewById(R.id.on_air_btn);
    requestCallBtn = getView().findViewById(R.id.request_call_btn);
    showArtProgress = getView().findViewById(R.id.show_art_progress);
    showArtProgress.show();
    final MainActivity mainActivity = (MainActivity) getActivity();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      playPauseBtn.setStateListAnimator(null);

    getContext().registerReceiver(showUpdateReceiver,
            new IntentFilter("UpdateShowInfo"));

    playPauseBtn.setOnClickListener(new View.OnClickListener() {
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
    public void onReceive(final Context context, Intent intent) {
        showTitleTv.setText(intent.getStringExtra("showTitle"));
        Picasso.get()
                .load(intent.getStringExtra("showArtUrl"))
                .into(showArtIv, new com.squareup.picasso.Callback() {
                  @Override
                  public void onSuccess() {
                      if (showArtProgress != null) showArtProgress.hide();

                      TypedValue value = new TypedValue();
                      context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);

                      Palette color = Palette
                              .from(((BitmapDrawable) showArtIv.getDrawable()).getBitmap())
                              .generate();

                      int newBgColor = color.getVibrantColor(value.data);
                      int newFgColor = color.getDarkVibrantColor(Color.parseColor("#FFFFFF"));
//                      int newColor = bgSwatch != null ? bgSwatch.getRgb() : value.data;
                      ValueAnimator bgAnim = new ValueAnimator();
                      bgAnim.setIntValues(
                              playPauseBtn.getBackgroundTintList().getDefaultColor(),
                              newBgColor
                      );
                      bgAnim.setEvaluator(new ArgbEvaluator());

                      bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                          playPauseBtn.setBackgroundTintList(
                                  ColorStateList.valueOf((int) valueAnimator.getAnimatedValue())
                          );
                        }
                      });
                      playPauseBtn.getDrawable().mutate().setColorFilter(newFgColor, PorterDuff.Mode.SRC_IN);

                      bgAnim.setDuration(500);
                        bgAnim.start();
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
        playPauseBtn.setImageDrawable
                (ContextCompat.getDrawable(context, R.drawable.baseline_pause_white_48));
      else
        playPauseBtn.setImageDrawable
                (ContextCompat.getDrawable(context, R.drawable.baseline_play_arrow_white_48));
    }
  };
}
