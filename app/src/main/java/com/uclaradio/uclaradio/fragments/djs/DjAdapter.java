package com.uclaradio.uclaradio.fragments.djs;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uclaradio.uclaradio.R;

import java.util.List;

public class DjAdapter extends RecyclerView.Adapter<DjAdapter.ViewHolder> {

  private List<DjData> items;

  private String baseUrl;

  DjAdapter(List<DjData> items, Context appContext) {
    this.items = items;
    baseUrl = appContext.getResources().getString(R.string.website);
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
    final ContentLoadingProgressBar progress = holder.image_progress;
    progress.show();
    String imageUrl = baseUrl + item.getPictureUrl();
    if (item.getPictureUrl() == null)
      imageUrl = baseUrl + "/img/bear_transparent.png";
    Picasso.get()
            .load(imageUrl)
            .resize(250, 250)
            .into(holder.imageView, new Callback() {
              @Override
              public void onSuccess() {
                progress.hide();
              }

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

  class ViewHolder extends RecyclerView.ViewHolder {

    private TextView text;
    private ImageView imageView;
    private ContentLoadingProgressBar image_progress;

    ViewHolder(View itemView) {
      super(itemView);
      this.text = itemView.findViewById(R.id.dj_username);
      this.imageView = itemView.findViewById(R.id.image);
      this.image_progress = itemView.findViewById(R.id.dj_image_progress);
    }
  }
}
