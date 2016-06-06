package hohostudio.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    RequestQueue mQueue = null;
    String url = "https://radio.chrislaganiere.net/api/schedule";
    StringRequest scheduleRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    android.util.Log.v("roger", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("shows");
                        int length = array.length();
                        ArrayList<ScheduleInfo> arrayList = new ArrayList<ScheduleInfo>();

                        android.util.Log.v("roger", "Array length is " + length);
                        for(int i = 0; i < length; i++) {
                            JSONObject curShow = array.getJSONObject(i);
                            String showTime = curShow.getString("time");
                            String showDay = curShow.getString("day");
                            String showTitle = curShow.getString("title");

                            String showGenre = "";
                            try {
                                showGenre = curShow.getString("genre");
                            } catch(Exception e) {
                                android.util.Log.v("roger", "");
                            }
                            JSONArray djs = curShow.getJSONArray("djs");
                            android.util.Log.v("roger", "Title is: " + showTitle + " Djs are: " + djs.toString() + " showDay is: " + showDay + " showTime is: " + showTime + " genre is: " + showGenre);
                            ScheduleInfo curInfo = new ScheduleInfo(showTitle, djs.getString(0), showGenre,showDay, showTime);
                            arrayList.add(curInfo);
                        }
                        ScheduleInfo scheduleArray[] = new ScheduleInfo[arrayList.size()];
                        scheduleArray = arrayList.toArray(scheduleArray);

                        mAdapter = new ScheduleAdapter(scheduleArray);
                        mRecyclerView.setAdapter(mAdapter);

                    } catch (Exception e) {
                        android.util.Log.v("roger", "json exception: " + e);
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.v("roger", error.toString());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mRecyclerView = (RecyclerView) findViewById(R.id.scheduleList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(mQueue == null) {
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mQueue = new RequestQueue(cache, network);
            mQueue.start();
        }
        mQueue.add(scheduleRequest);

        ScheduleInfo first = new ScheduleInfo("Show Name", "Host Name", "Genre", "Day", "Time");
        ScheduleInfo[] schedule = {first};

        mAdapter = new ScheduleAdapter(schedule);
        mRecyclerView.setAdapter(mAdapter);
    }


}
