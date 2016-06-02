package hohostudio.testapp;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class RecentlyPlayedAdapter extends RecyclerView.Adapter<RecentlyPlayedAdapter.ViewHolder> {
    private SongInfo[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout mRelativeLayout;
        public ViewHolder(RelativeLayout v) {
            super(v);
            mRelativeLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecentlyPlayedAdapter(SongInfo[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public RecentlyPlayedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recently_played_block, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder((RelativeLayout)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SongInfo item = mDataset[position];

        TextView songName = (TextView) holder.mRelativeLayout.findViewById(R.id.song_name);
        songName.setText(item.getSongName());

        TextView artistName = (TextView) holder.mRelativeLayout.findViewById(R.id.artist_name);
        artistName.setText(item.getArtistName());

        final ImageView imageView = (ImageView) holder.mRelativeLayout.findViewById(R.id.album_art);
        String url = item.getURL();
        if(url != "") {
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            MainActivity.mQueue.add(request);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
