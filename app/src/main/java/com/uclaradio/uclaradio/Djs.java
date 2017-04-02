package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
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
                        if(response.isSuccessful()) {

                            for(DjData dj : response.body().getDjList()) {
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

//        downloadDJs();
    }

    private void downloadDJs() {
        Ion.with(this)
                .load("https://uclaradio.com/api/djs")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // data has arrived
                        processDJs(result);
                    }
                });

    }

    private void processDJs(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONArray djs = json.getJSONArray("djs");
            for (int i = 0; i < djs.length(); i++) {
                JSONObject dj = djs.getJSONObject(i);
                if (dj.has("djName")) {
                    Log.i("tag", dj.getString("djName"));
                }
            }
        } catch (JSONException jsone) {
            Log.wtf("help", jsone);
        }
    }

}
