package com.uclaradio.uclaradio.Fragments.DJsFragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;

import java.util.List;

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
    if (item.getDjName() == null)
      holder.text.setText(item.getUsername());
    else
      holder.text.setText(item.getDjName());
    String imageUrl = "https://uclaradio.com" + item.getPictureUrl();
    if (item.getPictureUrl() == null)
      imageUrl = "https://uclaradio.com/img/bear_transparent.png";
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
