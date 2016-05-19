package pl.allenotify.anotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pl.allenotify.anotify.model.UserSearchContent;
import pl.allenotify.anotify.model.UserSearchContent.UserSearchItem;
import pl.allenotify.anotify.task.FetchItemList;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MainFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private MyMainRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MainFragment newInstance(int columnCount) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyMainRecyclerViewAdapter(UserSearchContent.items, mListener);
            recyclerView.setAdapter(mAdapter);
            registerForContextMenu(recyclerView);
            recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getActivity().openContextMenu(recyclerView);
                    return false;
                }
            });

        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh){
            refreshList();
            return true;
        }else if (id == R.id.action_add){
            Intent intent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
            startActivityForResult(intent, MainActivity.ADD_NEW_ITEM_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList(){
        FetchItemList f = new FetchItemList(getActivity().getApplicationContext(), recyclerView.getAdapter());
        f.execute();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.EDIT_ITEM_REQUEST) { //Użytkownik zaktualizował dane. Jeśli zmienił nazwę to aktualizujemy ją na liście
            if (resultCode == Activity.RESULT_OK) {
                String id = data.getStringExtra(MainActivity.INTENT_ITEM_ID);
                String name = data.getStringExtra(MainActivity.INTENT_ITEM_NAME);
                for (UserSearchItem item : UserSearchContent.items) {
                    if (item.getId().equals(id)) {
                        if (!item.getName().equals(name)) {
                            item.setName(name);
                            mAdapter.notifyItemChanged(UserSearchContent.items.indexOf(item));
                        }
                        break;
                    }
                }
                Toast.makeText(getActivity().getApplicationContext(), "Zaktualizowano", Toast.LENGTH_SHORT).show();
            }else if (resultCode == MainActivity.STATUS_ERROR){
                Toast.makeText(getActivity().getApplicationContext(), "Nieoczekiwany błąd", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == MainActivity.ADD_NEW_ITEM_REQUEST){
            refreshList();
            if (resultCode == MainActivity.STATUS_ERROR){
                Toast.makeText(getActivity().getApplicationContext(), "Nieoczekiwany błąd. Brak danych.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(UserSearchItem item);

        void onListFragmentStatusIconInteraction(UserSearchItem item);

        void onListLongClick(UserSearchItem item, View v);
     }


}
