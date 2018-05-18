package com.uclaradio.uclaradio.fragments.streaming;

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
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.activities.MainActivity;


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

  private static int REQUEST_PHONE_CALL;

  private OnFragmentInteractionListener mListener;

  public StreamingFragment() {
  }

  @SuppressWarnings("unused")
  public static StreamingFragment newInstance() {
    StreamingFragment fragment = new StreamingFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    REQUEST_PHONE_CALL = getResources().getInteger(R.integer.request_phone_call_id);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_streaming, container, false);
  }

  @SuppressWarnings("unused")
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
    getContext().unregisterReceiver(connRestReceiver);
    getContext().unregisterReceiver(connErrReceiver);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[],
                                         @NonNull int grantResults[]) {
      switch (requestCode) {
        case R.integer.request_phone_call_id:
          if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
              startActivity(callIntent);
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
              if (!shouldShowRequestPermissionRationale(permissions[0]))
              {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.calling_disabled)
                        .setMessage(R.string.no_perms_dialog)
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
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    playPauseBtn = view.findViewById(R.id.playpause_btn);
    showArtIv = view.findViewById(R.id.show_art_img);
    showTitleTv = view.findViewById(R.id.show_title_text);
    Button onAirCallBtn = view.findViewById(R.id.on_air_btn);
    Button requestCallBtn = view.findViewById(R.id.request_call_btn);
    showArtProgress = view.findViewById(R.id.show_art_progress);
    showArtProgress.show();
    final MainActivity mainActivity = (MainActivity) getActivity();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      playPauseBtn.setStateListAnimator(null);

    getContext().registerReceiver(showUpdateReceiver,
            new IntentFilter(getString(R.string.update_show_info_intent)));

    playPauseBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (!mainActivity.isBound()) {
          Log.d("Service", "Not yet bound");
          Snackbar.make(view, R.string.stream_not_loaded, Snackbar.LENGTH_LONG)
                  .show();
          return;
        }
        if (MainActivity.stream == null) {
          Log.d("Service", "Stream is null...");
          Snackbar.make(view, "The stream hasn't loaded yet. Try again after a few seconds!", Snackbar.LENGTH_LONG)
                  .show();
          return;
        }
//        if (MainActivity.stream.isPlaying())
//          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
//        else
//          ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
//        MainActivity.stream.toggle();
        getContext().sendBroadcast(new Intent(getString(R.string.play_pause_intent)));
      }
    });

    onAirCallBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          callIntent.setData(Uri.parse(getString(R.string.tel_onair)));
          if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
          else startActivity(callIntent);
      }
    });

    requestCallBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callIntent.setData(Uri.parse(getString(R.string.tel_request)));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
          ActivityCompat.requestPermissions(getActivity(),
                  new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        else startActivity(callIntent);
      }
    });

    getContext().registerReceiver(connErrReceiver,
            new IntentFilter(getString(R.string.connection_error)));
    getContext().registerReceiver(connRestReceiver,
            new IntentFilter(getString(R.string.connection_restored)));
    getContext().registerReceiver(toggleReceiver,
            new IntentFilter(getString(R.string.play_pause_intent)));
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }

  private BroadcastReceiver showUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, Intent intent) {
        showTitleTv.setText(intent.getStringExtra(getString(R.string.extra_showTitle)));
        Picasso.get()
                .load(intent.getStringExtra(getString(R.string.extra_showArtUrl)))
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
                      int newFgColor = color
                              .getDarkVibrantColor(getResources().getColor(android.R.color.white));
//                      int newColor = bgSwatch != null ? bgSwatch.getRgb() : value.data;
                      ValueAnimator bgAnim = new ValueAnimator();
                      // Background tint list should never be null, so this is f
                      //noinspection ConstantConditions
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

  private void togglePlayButton() {
    if (MainActivity.stream != null && MainActivity.stream.isPlaying())
      playPauseBtn.setImageResource(R.drawable.baseline_pause_white_48);
    else
      playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_white_48);
    if (showArtIv.getDrawable() != null) {
      Palette color = Palette
              .from(((BitmapDrawable) showArtIv.getDrawable()).getBitmap())
              .generate();
      int newFgColor = color.getDarkVibrantColor(getResources().getColor(android.R.color.white));
      playPauseBtn.getDrawable().mutate().setColorFilter(newFgColor, PorterDuff.Mode.SRC_IN);
    }
  }

  private BroadcastReceiver toggleReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        togglePlayButton();
    }
  };

  private BroadcastReceiver connErrReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        playPauseBtn.setImageResource(R.drawable.baseline_autorenew_white_48);
        if (showArtIv.getDrawable() != null) {
          Palette color = Palette
                  .from(((BitmapDrawable) showArtIv.getDrawable()).getBitmap())
                  .generate();
          int newFgColor = color.getDarkVibrantColor(getResources().getColor(android.R.color.white));
          playPauseBtn.getDrawable().mutate().setColorFilter(newFgColor, PorterDuff.Mode.SRC_IN);
        }
        Log.d("Service", "Connection error.");
    }
  };

  private BroadcastReceiver connRestReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      togglePlayButton();
      Log.d("Service", "Connection restored.");
    }
  };
}
