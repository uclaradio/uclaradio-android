package com.uclaradio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.*;

public class Djs extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_djs);

        downloadDJs();
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
                Log.i("tag", dj.getString("username"));
            }
        } catch (JSONException jsone) {
            Log.wtf("help", jsone);
        }
    }

}
