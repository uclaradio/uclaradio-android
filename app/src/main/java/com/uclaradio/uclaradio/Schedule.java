package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Schedule extends AppCompatActivity {

    private RadioPlatform platform;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = (RecyclerView) findViewById(R.id.shows_ids_rv);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://uclaradio.com/")
                .build();

        platform = retrofit.create(RadioPlatform.class);

        platform.getSchedules()
                .enqueue(new Callback<ScheduleList>() {
                    @Override
                    public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {
                        if(response.isSuccessful()) {
                            for (ScheduleData show : response.body().getScheduleList())
                            {
                                Log.d("TAG", "SHOW NAME IS " + show.getTitle());
                                Log.d("TAG", "Time: " + show.getTime());
                                Log.d("TAG", "Day: " + show.getDay());
                                Log.d("TAG", "Genre: " + show.getGenre());
                            }

                        } else {
                            Log.e("TAG", "HERE FAILED");
                        }
                    }

                    @Override
                    public void onFailure(Call<ScheduleList> call, Throwable t) {

                    }


                });
    }
}
