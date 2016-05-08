package pl.allenotify.anotify;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.contextMenuLong(holder.mItem, v);
                return true;
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

        public ViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mCountFoundView = (TextView) itemView.findViewById(R.id.count_found);
            mDateView = (TextView) itemView.findViewById(R.id.date);
            mStatusView = (ImageView) itemView.findViewById(R.id.list_status_icon);

            mView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    menu.add(MainActivity.appContext.getString(R.string.context_menu_remove)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            DeleteItem task = new DeleteItem();
                            task.execute(mItem.getId());
                            return false;
                        }
                    });
                }
            });

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public class DeleteItem extends AsyncTask<String, Void, String> {

        private String LOG_TAG = DeleteItem.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;

            if (params.length == 0)
                return null;
            String id = params[0];
            try {
                Uri.Builder builder = new Uri.Builder();


                builder.scheme("http")
                        .authority("webapi.allenotify.pl")
                        .appendPath("SearchItem")
                        .appendPath(id);


                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.appContext
                        .getApplicationContext());
                String token = sharedPrefs.getString(MainActivity.appContext
                        .getString(R.string.prefs_access_token_key), "");
                //Log.v(LOG_TAG, "TOKEN:" + token);
                urlConnection.setRequestProperty("Authorization", "bearer " + token);

                urlConnection.connect();
                Log.v(LOG_TAG, "RESPONSE CODE: " + String.valueOf(urlConnection.getResponseCode()));
                Log.v(LOG_TAG, "RESPONSE MESSAGE: " + urlConnection.getResponseMessage());

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR! No connection?", e);
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            return id;
        }

        @Override
        protected void onPostExecute(String s) {
            for (UserSearchItem item : mValues){
                if (item.getId().equals(s)) {
                    int position = mValues.indexOf(item);
                    mValues.remove(item);
                    notifyItemRemoved(position);
                    break;
                }
            }

        }
    }
}
