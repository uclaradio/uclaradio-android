package hohostudio.testapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private ScheduleInfo[] mDataset;

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
    public ScheduleAdapter(ScheduleInfo[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_block, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder((RelativeLayout)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ScheduleInfo item = mDataset[position];

        TextView showDay = (TextView) holder.mRelativeLayout.findViewById(R.id.show_day);
        showDay.setText(item.getShowDay());

        TextView showTime = (TextView) holder.mRelativeLayout.findViewById(R.id.show_time);
        showTime.setText(item.getShowTime());

        TextView showName = (TextView) holder.mRelativeLayout.findViewById(R.id.show_name);
        showName.setText(item.getShowName());

        TextView hostName = (TextView) holder.mRelativeLayout.findViewById(R.id.host_name);
        hostName.setText(item.getHostName());

        TextView genreName = (TextView) holder.mRelativeLayout.findViewById(R.id.genre_name);
        genreName.setText(item.getGenreName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
