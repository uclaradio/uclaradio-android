package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Schedule extends AppCompatActivity {

    private RadioPlatform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://uclaradio.com/")
                .build();

        platform = retrofit.create(RadioPlatform.class);

        platform.getSchedules()
                .enqueue(new Callback<ShowSchedule>() {
                    @Override
                    public void onResponse(Call<ShowSchedule> call, Response<ShowSchedule> response) {
                        if(response.isSuccessful()) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ShowSchedule> call, Throwable t) {

                    }
                });
    }
}
