package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Djs extends AppCompatActivity {

    private RadioPlatform platform;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_djs);

        recyclerView = (RecyclerView) findViewById(R.id.dj_ids_rv);
        final int numberOfCols = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfCols));


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
                                    new LinearLayoutManager(Djs.this, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(new GridLayoutManager(Djs.this, numberOfCols));
                            DjAdapter adapter = new DjAdapter(response.body().getDjList());
                            recyclerView.setAdapter(adapter);
                            for (DjData dj : response.body().getDjList()) {
                                Log.d("TAG", "DJ NAME IS: " + dj.getUsername());
                            }
                        } else {
                            Log.e("TAG", "HERE FAILED");
                        }
                    }

                    @Override
                    public void onFailure(Call<DjList> call, Throwable t) {
                        Log.e("TAG", "FAILED TO MAKE API CALL");
                    }
                });
    }
}
