package com.uclaradio.uclaradio.Fragments.DJsFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.uclaradio.uclaradio.RadioPlatform;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DJsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DJsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DJsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private RadioPlatform platform;
    private RecyclerView recyclerView;

    private ContentLoadingProgressBar djsProgress;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int numberOfCols = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DJsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DJsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DJsFragment newInstance(String param1, String param2) {
        DJsFragment fragment = new DJsFragment();
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
        return inflater.inflate(R.layout.fragment_djs, container, false);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = getView().findViewById(R.id.dj_ids_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfCols));
        djsProgress = getView().findViewById(R.id.djs_progress);
        djsProgress.show();

        getDjs();
    }

    private void getDjs() {
        Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://uclaradio.com/")
        .build();

        platform = retrofit.create(RadioPlatform.class);

        platform.getDjs()
                .enqueue(new Callback<DjList>() {
                    @Override
                    public void onResponse(Call<DjList> call, Response<DjList> response) {
                        if (response.isSuccessful()) {
                            LinearLayoutManager linearLayoutManager =
                                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfCols));
                            DjAdapter adapter = new DjAdapter(response.body().getDjList());
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
                        getDjs();
                    }
                });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
