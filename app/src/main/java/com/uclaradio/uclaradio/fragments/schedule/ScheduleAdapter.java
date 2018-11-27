package com.uclaradio.uclaradio.fragments.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
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

  private static final int DAYS_IN_WEEK = 7;

  private Context context;
  private Resources resources;

  private String baseUrl;

  private int convertDayToNum(String day) {
    return dayToNum.get(day);
  }

  ScheduleAdapter(List<ScheduleData> items, Context appContext) {
    context = appContext;
    resources = context.getResources();
    baseUrl = resources.getString(R.string.website);

    int numCols = appContext.getResources().getInteger(R.integer.num_show_cols);

    String[] days = new String[] {
      "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    for (int i = 0; i < DAYS_IN_WEEK; i++) {
      dayToNum.put(days[i], i);
      dayToLongDay.put(days[i], resources.getStringArray(R.array.days_of_week)[i]);
    }

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
          @SuppressLint("SimpleDateFormat")
          SimpleDateFormat sdf = new SimpleDateFormat("hhaa"); //hourAM/PM
          try {
            Date aTime = sdf.parse(a.getTime().toUpperCase());
            Date bTime = sdf.parse(b.getTime().toUpperCase());

            return aTime.compareTo(bTime);
          } catch (ParseException ex) {
              return a.getTime().compareTo(b.getTime());
          }
        }
      }
    };
    Collections.sort(items, dateComparator);

    for (int i = 0; i < items.size(); i++) {
      ScheduleData item = items.get(i);
      // What's going on here:
      //   This code is just checking if the current show would normally be displayed on the rightmost
      //   column but is on a day different from the preceding show. Then it'll push it onto a new
      //   row and plop a null object where the show used to be.
      // Why do this?
      //   The labels for each day in the RecyclerView are actually part of an item being displayed;
      //   this code is here in order to make sure each new day starts on a new row.
      if (i % numCols == 1 && (items.get(i-1) != null && !item.getDay().equals(items.get(i-1).getDay()))) {
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
        @SuppressLint("InflateParams")
        View sheetView = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.show_bottom_sheet_layout, null);

        ImageView showImage = sheetView.findViewById(R.id.show_image);

        TextView showTitle = sheetView.findViewById(R.id.show_title);
        TextView showGenre = sheetView.findViewById(R.id.show_genre);
        TextView showTime  = sheetView.findViewById(R.id.show_time);
        TextView showDjs   = sheetView.findViewById(R.id.show_djs);
        TextView showBlurb = sheetView.findViewById(R.id.show_blurb);


        String imageUrl = baseUrl + show.getPictureUrl();
        if (show.getPictureUrl() == null)
          imageUrl = baseUrl + "/img/radio.png";
        Picasso.get()
                .load(imageUrl)
                .resize(250, 250)
                .into(showImage);
        showTitle.setText(show.getTitle());
        showTime.setText(resources.getString(R.string.time_and_day, dayToLongDay.get(show.getDay()), show.getTime()));
//        showTime.setText(dayToLongDay.get(show.getDay()) + "s at " + show.getTime());

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

    String imageUrl = baseUrl + item.getPictureUrl();
    if (item.getPictureUrl() == null)
      imageUrl = baseUrl + "/img/radio.png";

    Picasso.get()
            .load(imageUrl)
            .resize(dpToPx(250), dpToPx(250))
            .into(holder.image_show, new Callback() {
              @Override
              public void onSuccess() {}

              @Override
              public void onError(Exception e) {
                e.printStackTrace();
              }
            });
  }

  @Override
  public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  @SuppressWarnings("SameParameterValue")
  private int dpToPx(int dp) {
    return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().getDisplayMetrics()
    );
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView text_day;
    private TextView text_details;
    private ImageView image_show;

    ViewHolder(View itemView) {
      super(itemView);
      this.image_show = itemView.findViewById(R.id.schedule_image);
      this.text_details = itemView.findViewById(R.id.schedule_details);
      this.text_day = itemView.findViewById(R.id.schedule_day);
    }
  }
}
