package pl.allenotify.anotify;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.allenotify.anotify.model.UserSearchContent.UserSearchItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserSearchItem} and makes a call to the
 * specified {@link pl.allenotify.anotify.MainFragment.OnListFragmentInteractionListener}.
 */
public class MyMainRecyclerViewAdapter extends RecyclerView.Adapter<MyMainRecyclerViewAdapter.ViewHolder> {

    private final List<UserSearchItem> mValues;
    private final MainFragment.OnListFragmentInteractionListener mListener;

    public MyMainRecyclerViewAdapter(List<UserSearchItem> items, MainFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mCountFoundView.setText(mValues.get(position).getCountFound());
        holder.mDateView.setText(mValues.get(position).getDate().toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentStatusIconInteraction(holder.mItem);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mCountFoundView;
        public final TextView mDateView;
        public final ImageView mStatusView;
        public UserSearchItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mCountFoundView = (TextView) itemView.findViewById(R.id.count_found);
            mDateView = (TextView) itemView.findViewById(R.id.date);
            mStatusView = (ImageView) itemView.findViewById(R.id.list_status_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
