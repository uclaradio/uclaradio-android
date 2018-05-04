package com.uclaradio.uclaradio.Fragments.ScheduleFragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

  private List<ScheduleData> items;
  private HashMap<String, Integer> dayToNum = new HashMap<>();
  private HashMap<String, String> dayToLongDay = new HashMap<>();

  private int convertDayToNum(String day) {
    return dayToNum.get(day);
  }

  public ScheduleAdapter(List<ScheduleData> items) {
    dayToNum.put("Sun",0); dayToLongDay.put("Sun", "Sunday");
    dayToNum.put("Mon",1); dayToLongDay.put("Mon", "Monday");
    dayToNum.put("Tue",2); dayToLongDay.put("Tue", "Tuesday");
    dayToNum.put("Wed",3); dayToLongDay.put("Wed", "Wednesday");
    dayToNum.put("Thu",4); dayToLongDay.put("Thu", "Thursday");
    dayToNum.put("Fri",5); dayToLongDay.put("Fri", "Friday");
    dayToNum.put("Sat",6); dayToLongDay.put("Sat", "Saturday");

    Comparator<ScheduleData> dateComparator = new Comparator<ScheduleData>() {
      @Override
      public int compare(ScheduleData a, ScheduleData b) {
        Integer aDay = convertDayToNum(a.getDay());
        Integer bDay = convertDayToNum(b.getDay());
        Integer dayComp = aDay.compareTo(bDay);
        if (dayComp != 0) {
          return dayComp;
        }
        else {
          SimpleDateFormat sdf = new SimpleDateFormat("hhaa"); //hourAM/PM
          try {
            Date aTime = sdf.parse(a.getTime().toUpperCase());
            Log.d("aTime", aTime.toString());
            Date bTime = sdf.parse(b.getTime().toUpperCase());
            Log.d("bTime", bTime.toString());

            return aTime.compareTo(bTime);
          } catch (ParseException ex) {
              Log.e("ERROR", ex.getMessage());
              return a.getTime().compareTo(b.getTime());
          }
        }
      }
    };
    Collections.sort(items, dateComparator);

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
    holder.setIsRecyclable(false);
    ScheduleData item = items.get(holder.getAdapterPosition());
    holder.text_title.setText(item.getTitle());
    holder.text_time.setText(item.getTime());
    holder.text_day.setText(dayToLongDay.get(item.getDay()));

    if(item.getGenre() == null) {
      holder.text_genre.setVisibility(View.GONE);
    } else {
      holder.text_genre.setText(item.getGenre());
    }

    // If it's the first item in the list or if the day doesn't match the previous
    //  entry's day
    if (holder.getAdapterPosition() == 0
            || !item.getDay().equals(items.get(holder.getAdapterPosition()-1).getDay())) {
        holder.text_day.setVisibility(View.VISIBLE);
    }

    String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
    if (item.getPictureUrl() == null)
      imageUrl = "https://uclaradio.com/img/bear_transparent.png";
    Log.d("TAG", "ALBUM IMAGE URL: " + imageUrl);

    Picasso.get()
            .load(imageUrl)
            .resize(250, 250)
            .into(holder.image_show);
  }

  @Override
  public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView text_title;
    private TextView text_genre;
    private TextView text_time;
    private TextView text_day;
    private ImageView image_show;

    public ViewHolder(View itemView) {
      super(itemView);
      this.text_title = itemView.findViewById(R.id.schedule_title);
      this.text_time = itemView.findViewById(R.id.schedule_time);
      this.text_genre = itemView.findViewById(R.id.schedule_genre);
      this.image_show = itemView.findViewById(R.id.schedule_image);
      this.text_day = itemView.findViewById(R.id.schedule_day);
    }
  }
}
