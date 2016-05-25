package hohostudio.uclaradio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mRecyclerView = (RecyclerView) findViewById(R.id.scheduleList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ScheduleInfo first = new ScheduleInfo("9am", "The Kellzi Show", "Alec", "Talk");
        ScheduleInfo second = new ScheduleInfo("10am", "Seasonal Soundwaves", "Hannah", "Indie/Pop/TV");
        ScheduleInfo third = new ScheduleInfo("11am", "Derivative", "DJ umlaut", "Hip-Hop");
        ScheduleInfo four = new ScheduleInfo("12pm", "The Discourse", "Alex Torpey", "Talk");
        ScheduleInfo five = new ScheduleInfo("1pm", "Beneath the Beach", "DJ Shaggy", "Garage/Surf Rock");
        ScheduleInfo six = new ScheduleInfo("2pm", "The Awakening", "Lord Trav", "Hip-Hop/R&B");


        ScheduleInfo[] schedule = {first, second, third, four, five, six};


        mAdapter = new ScheduleAdapter(schedule);
        mRecyclerView.setAdapter(mAdapter);
    }
}
