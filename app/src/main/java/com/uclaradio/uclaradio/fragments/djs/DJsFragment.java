package com.uclaradio.uclaradio.fragments.djs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.interfaces.RadioPlatform;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DJsFragment extends Fragment {
    private RecyclerView recyclerView;

    private ContentLoadingProgressBar djsProgress;

    private final int numberOfCols = 2;
    private long pollRate = 1000;

    private OnFragmentInteractionListener mListener;

    public DJsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_djs, container, false);
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.dj_ids_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfCols));
        djsProgress = view.findViewById(R.id.djs_progress);
        djsProgress.show();

        getDjs();
    }

    private void getDjs() {
        Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(getString(R.string.website))
        .build();

        RadioPlatform platform = retrofit.create(RadioPlatform.class);

        platform.getDjs()
                .enqueue(new Callback<DjList>() {
                    @Override
                    public void onResponse(Call<DjList> call, Response<DjList> response) {
                        if (response.isSuccessful()) {
//                            LinearLayoutManager manager =
//                                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            GridLayoutManager manager =
                                    new GridLayoutManager(getContext(), numberOfCols, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                            DjAdapter adapter = new DjAdapter(response.body().getDjList(), getContext());
                            recyclerView.setAdapter(adapter);
                            for (DjData dj : response.body().getDjList()) {
                                Log.d("TAG", "DJ NAME IS: " + dj.getUsername());
                            }
                            djsProgress.hide();
                        } else {
                            Log.e("TAG", "HERE FAILED");
                            getDjs();
                        }
                    }

                    @Override
                    public void onFailure(Call<DjList> call, Throwable t) {
                        Log.e("TAG", "FAILED TO MAKE API CALL");
                        // Double the delay to try again, then try again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pollRate *= 2;
                                getDjs();
                            }
                        }, pollRate);
                    }
                });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
