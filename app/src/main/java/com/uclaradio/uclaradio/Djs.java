package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
