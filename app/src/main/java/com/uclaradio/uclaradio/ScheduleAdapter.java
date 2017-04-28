package com.uclaradio.uclaradio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleData> items;

    public ScheduleAdapter(List<ScheduleData> items) {
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
        //String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
        //Log.d("TAG", "ALBUM IMAGE URL: " + imageUrl);
//        Picasso.with(holder.text_title.getContext())
//                .load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_title;
        public ViewHolder(View itemView) {
            super(itemView);
            this.text_title = (TextView) itemView.findViewById(R.id.schedule_title);
        }
    }
}
