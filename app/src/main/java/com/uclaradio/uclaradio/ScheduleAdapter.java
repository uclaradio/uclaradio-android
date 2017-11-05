package com.uclaradio.uclaradio;

import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleData> items;
    HashMap<String, Integer> dayToNum = new HashMap<String, Integer>();

    public int convertDayToNum(String day){
        return dayToNum.get(day);
    }

    public ScheduleAdapter(List<ScheduleData> items) {

        dayToNum.put("Sun",0);
        dayToNum.put("Mon",1);
        dayToNum.put("Tue",2);
        dayToNum.put("Wed",3);
        dayToNum.put("Thu",4);
        dayToNum.put("Fri",5);
        dayToNum.put("Sat",6);

        Comparator<ScheduleData> dateComparator = new Comparator<ScheduleData>() {
            @Override
            public int compare(ScheduleData a, ScheduleData b) {
                Integer aDay = convertDayToNum(a.getDay());
                Integer bDay = convertDayToNum(b.getDay());
                Integer dayComp = aDay.compareTo(bDay);
                if (dayComp != 0){
                    return dayComp;
                }
                else {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy"); //hourAM/PM
                    String aTime = a.getTime().toUpperCase();
                    Log.d("aTime", aTime);
                    String bTime = b.getTime().toUpperCase();
                    Log.d("bTime", bTime);
//                    Date aTimeD = sdf.parse("Thu Jun 18 20:56:02 EDT 2009");
                    return a.getTime().compareTo(b.getTime());
                }
            }
        };
        Collections.sort(items, dateComparator);



//        Collections.sort(items, new Comparator<ScheduleData>(){
//            public int compare(ScheduleData a, ScheduleData b){
//
//
//
//                return a.getDay().compareTo(b.getDay());
//            }
//        });
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleData item = items.get(position);
        holder.text_title.setText(item.getTitle());
        holder.text_time.setText(item.getTime());
        holder.text_genre.setText(item.getGenre());
        String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
        if (item.getPictureUrl() == null)
            imageUrl = "https://raw.githubusercontent.com/uclaradio/uclaradio-iOS/master/UCLA%20Radio/UCLA%20Radio/images/radio.png";
        Log.d("TAG", "ALBUM IMAGE URL: " + imageUrl);
        Picasso.with(holder.text_title.getContext())
                .load(imageUrl).into(holder.image_show);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_title;
        private TextView text_genre;
        private TextView text_time;
        private ImageView image_show;

        public ViewHolder(View itemView) {
            super(itemView);
            this.text_title = (TextView) itemView.findViewById(R.id.schedule_title);
            this.text_time = (TextView) itemView.findViewById(R.id.schedule_time);
            this.text_genre = (TextView) itemView.findViewById(R.id.schedule_genre);
            this.image_show = (ImageView) itemView.findViewById(R.id.schedule_image);
        }
    }
}
