package com.uclaradio.uclaradio;

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

public class DjAdapter extends RecyclerView.Adapter<DjAdapter.ViewHolder> {

    private List<DjData> items;

    public DjAdapter(List<DjData> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dj_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DjData item = items.get(position);
        holder.text.setText(item.getUsername());
        String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
        Log.d("TAG", "IMAGE URL: " + imageUrl);
        Picasso.with(holder.text.getContext())
                .load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.dj_username);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
