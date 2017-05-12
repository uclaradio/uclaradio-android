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
