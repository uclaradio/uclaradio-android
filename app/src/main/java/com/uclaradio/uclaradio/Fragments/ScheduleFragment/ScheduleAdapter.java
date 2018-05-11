package com.uclaradio.uclaradio.Fragments.ScheduleFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
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

  private Context context;

  private int convertDayToNum(String day) {
    return dayToNum.get(day);
  }

  public ScheduleAdapter(List<ScheduleData> items, Context appContext) {
    context = appContext;

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

    for (int i = 0; i < items.size(); i++) {
      ScheduleData item = items.get(i);
      // Some trickery going on here
      if (i % 2 == 1 && (items.get(i-1) != null && !item.getDay().equals(items.get(i-1).getDay()))) {
        items.add(i, null);
      }
    }

    this.items = items;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.schedule_item, parent, false);
                    .inflate(R.layout.schedule_item_new, parent, false);
    final ViewHolder holder = new ViewHolder(itemView);
    itemView.setOnClickListener(new View.OnClickListener() {
      @SuppressLint("SetTextI18n")
      @Override
      public void onClick(View view) {
        int position = holder.getAdapterPosition();
        ScheduleData show = items.get(position);
        if (show == null) return;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View sheetView = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.show_bottom_sheet_layout, null);

        ImageView showImage = sheetView.findViewById(R.id.show_image);

        TextView showTitle = sheetView.findViewById(R.id.show_title);
        TextView showGenre = sheetView.findViewById(R.id.show_genre);
        TextView showTime  = sheetView.findViewById(R.id.show_time);
        TextView showDjs   = sheetView.findViewById(R.id.show_djs);
        TextView showBlurb = sheetView.findViewById(R.id.show_blurb);


        String imageUrl = "https://uclaradio.com" + show.getPictureUrl();
        if (show.getPictureUrl() == null)
          imageUrl = "https://uclaradio.com/img/radio.png";
        Picasso.get()
                .load(imageUrl)
                .resize(250, 250)
                .into(showImage);
        showTitle.setText(show.getTitle());
        showTime.setText(dayToLongDay.get(show.getDay()) + "s at " + show.getTime());

        if (show.getGenre() == null)
          showGenre.setVisibility(View.GONE);
        else
          showGenre.setText(show.getGenre());

        if (show.getBlurb() == null)
          showBlurb.setVisibility(View.GONE);
        else
          showBlurb.setText(show.getBlurb());

        showDjs.setText(show.getDjs());

        dialog.setContentView(sheetView);
        dialog.show();
      }
    });

    return holder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setIsRecyclable(false);
    int pos = holder.getAdapterPosition();
    ScheduleData item = items.get(pos);
    if (item == null) return;
    StringBuilder builder = new StringBuilder();
    holder.text_day.setText(dayToLongDay.get(item.getDay()));
    builder.append(item.getTime()).append(": ")
            .append(item.getTitle()).append(" | ")
            .append(item.getDjs());
    holder.text_details.setText(builder.toString());

    if (pos == 0
            || items.get(pos-1) == null
            || !item.getDay().equals(items.get(pos-1).getDay())) {
      holder.text_day.setVisibility(View.VISIBLE);
      item.setIsNewDay(true);
    }

    if (pos > 0
            && (items.get(pos-1) != null && items.get(pos-1).isOnNewDay())) {
        holder.text_day.setVisibility(View.INVISIBLE);
    }

    String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
    if (item.getPictureUrl() == null)
      imageUrl = "https://uclaradio.com/img/radio.png";
    Log.d("TAG", "ALBUM IMAGE URL: " + imageUrl);

    Picasso.get()
            .load(imageUrl)
            .resize(dpToPx(250), dpToPx(250))
            .into(holder.image_show, new Callback() {
              @Override
              public void onSuccess() {}

              @Override
              public void onError(Exception e) {
                Log.e("Picasso", "Error in Picasso!");
                e.printStackTrace();
              }
            });
  }

  //region Old onBindViewHolder
  /*
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setIsRecyclable(false);
    ScheduleData item = items.get(holder.getAdapterPosition());
    holder.text_title.setText(item.getTitle());
    holder.text_time.setText(item.getTime());
    holder.text_day.setText(dayToLongDay.get(item.getDay()));
//    final ContentLoadingProgressBar progress = holder.image_progress;
//    progress.show();

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
      imageUrl = "https://uclaradio.com/img/radio.png";
    Log.d("TAG", "ALBUM IMAGE URL: " + imageUrl);

    Picasso.get()
            .load(imageUrl)
            .resize(250, 250)
            .into(holder.image_show, new Callback() {
              @Override
              public void onSuccess() {
//                progress.hide();
              }

              @Override
              public void onError(Exception e) {
                Log.e("Picasso", "Error in Picasso!");
                e.printStackTrace();
              }
            });
  }
  */
  //endregion

  @Override
  public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  private int dpToPx(int dp) {
    return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().getDisplayMetrics()
    );
  }

  class ViewHolder extends RecyclerView.ViewHolder {
//    private TextView text_title;
//    private TextView text_genre;
//    private TextView text_time;
    private TextView text_day;
    private TextView text_details;
    private ImageView image_show;

    public ViewHolder(View itemView) {
      super(itemView);
//      this.text_title = itemView.findViewById(R.id.show_title);
//      this.text_time = itemView.findViewById(R.id.show_time);
//      this.text_genre = itemView.findViewById(R.id.show_genre);
      this.image_show = itemView.findViewById(R.id.schedule_image);
      this.text_details = itemView.findViewById(R.id.schedule_details);
      this.text_day = itemView.findViewById(R.id.schedule_day);
    }
  }
}
