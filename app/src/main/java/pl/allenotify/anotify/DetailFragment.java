package pl.allenotify.anotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.allenotify.anotify.model.CategoryContent;
import pl.allenotify.anotify.model.SearchDetailContent;
import pl.allenotify.anotify.task.GetTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements GetTask.GetTaskCaller {

    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "name";
    List<Spinner> categorySpinners = new ArrayList<>();
    private LinearLayout mCatGroup;
    SearchDetailContent.SearchDetailItem mSearchDetailItem = null;

    private String searchId;
    private String searchName;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String id, String name) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString(ARG_PARAM1);
            searchName = getArguments().getString(ARG_PARAM2);
            GetTask getSearchDetailTask = new GetTask();
            getSearchDetailTask.registerListener(this);
            getSearchDetailTask.execute("http://webapi.allenotify.pl/SearchItem/1183");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detail, container, false);

        mCatGroup = (LinearLayout) view.findViewById(R.id.spinnerCategoryGroupLayout);

        //Spinner z typem sprzedaży Kup Teraz / Licytacja
        Spinner spinner = (Spinner)view.findViewById(R.id.detail_sales_format_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Kategorie
        //categorySpinners.add((Spinner) view.findViewById(R.id.detail_main_category_spinner));
        //List<CategoryContent.CategoryItem> categoryList = new ArrayList<>();

        //Stan przedmiotu
        Spinner itemState = (Spinner) view.findViewById(R.id.detail_item_state_spinner);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.item_states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemState.setAdapter(stateAdapter);

        //Pobranie głównych kategorii
        //FetchCategoryTask task= new FetchCategoryTask("0", categoryList);
        //task.execute();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void fillViewsWithData(){
        TextView searchNameView = (TextView)getActivity().findViewById(R.id.detail_search_name);
        searchNameView.setText(mSearchDetailItem.getName());

        TextView titleView = (TextView)getActivity().findViewById(R.id.detail_item_title) ;
        titleView.setText(mSearchDetailItem.getTitle());

        TextView minimalPriceView = (TextView)getActivity().findViewById(R.id.detail_minimal_price);
        minimalPriceView.setText(mSearchDetailItem.getPriceMin().toString());

        TextView maximumPriceView = (TextView)getActivity().findViewById(R.id.detail_maximum_price);
        maximumPriceView.setText(mSearchDetailItem.getPriceMax().toString());

        //Stan przedmiotu
        Spinner itemState = (Spinner) getActivity().findViewById(R.id.detail_item_state_spinner);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.item_states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemState.setAdapter(stateAdapter);
        itemState.setSelection(mSearchDetailItem.getConditionId());

        //Spinner z typem sprzedaży Kup Teraz / Licytacja
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.detail_sales_format_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        itemState.setSelection(mSearchDetailItem.getOfferTypeId());

        //Kategorie
        for (SearchDetailContent.ChosenCategory chosenCategory : mSearchDetailItem.getSelectedCategories()){
            addCategorySpinner(chosenCategory.getSiblings(), chosenCategory.getCategoryId().toString());
            categorySpinners.get(categorySpinners.size()-1).setSelection(chosenCategory.getCategoryPositionOnSiblings());
            //break;
        }




    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Metody wywoływana przy wybraniu pozycji na spinerze kategorii.
     * Inicjuje pobieranie podkategorii lub usuwa niepotrzebne Spinnery
     * @param view Spinner na ktory klienieto
     * @param id id pozycji ktora wybrano
     */
    private void chooseCategory(Spinner view, long id ){
        int spinnerId = categorySpinners.indexOf(view);
        if (spinnerId < categorySpinners.size()-1){
            for (int i = categorySpinners.size()-1; i > spinnerId; i--){
                mCatGroup.removeView(categorySpinners.get(i));
                categorySpinners.remove(i);
            }
        }
        if (id > 0) {
            CategoryContent.CategoryItem item = (CategoryContent.CategoryItem) view.getSelectedItem();
            List<CategoryContent.CategoryItem> cat = new ArrayList<>();
            FetchCategoryTask t = new FetchCategoryTask(item.getId(), cat);
            t.execute();
        }
    }

    @Override
    public void asyncTaskDone(String response) {
        try {
            parseSearchItemDetailResponse(response);
            fillViewsWithData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dodaje nowy Spinner kategorii i zasila go listą.
     * Jeśli
     * @param categoryItems lista którą zasilany będzie spinner
     * @param categoryId id wybranej kategorii, dla której dzieci są w liście categoryItems
     */
    private void addCategorySpinner(List<CategoryContent.CategoryItem> categoryItems, String categoryId){
        if (categoryItems != null) {

            ArrayAdapter<CategoryContent.CategoryItem> categoriesAdapter = new ArrayAdapter<CategoryContent.CategoryItem>(
                    getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    categoryItems);

            /**
             * Przy tworzeniu widoku, pobieramy kategorie podając id=0.
             * Dla tego przypadku nie dodajemy nowego Spinner ponieważ jest on na stałe widoku.
             */
            if (categoryId == null || "0".equals(categoryId)) {
                categorySpinners.get(0).setAdapter(categoriesAdapter);
            } else if (!categoryItems.isEmpty()) {
                Spinner spinner = new Spinner(getActivity());
                spinner.setAdapter(categoriesAdapter);
                mCatGroup.addView(spinner);
                categorySpinners.add(spinner);
            }

            SpinnerInteractionListener listener = new SpinnerInteractionListener();
            categorySpinners.get(categorySpinners.size() - 1).setOnTouchListener(listener);
            categorySpinners.get(categorySpinners.size() - 1).setOnItemSelectedListener(listener);
        }
    }

    private void parseSearchItemDetailResponse(String response) throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        mSearchDetailItem = new SearchDetailContent.SearchDetailItem();
        mSearchDetailItem.setName(responseObject.getString(SearchDetailContent.NAME));
        mSearchDetailItem.setTitle(responseObject.getString(SearchDetailContent.TITLE));
        mSearchDetailItem.setSearchInDesc(responseObject.getBoolean(SearchDetailContent.SEARCH_IN_DESC));
        mSearchDetailItem.setPriceMin(responseObject.getDouble(SearchDetailContent.PRICE_MIN));
        mSearchDetailItem.setPriceMax(responseObject.getDouble(SearchDetailContent.PRICE_MAX));
        mSearchDetailItem.setCategoryId(responseObject.getInt(SearchDetailContent.CATEGORY_ID));
        mSearchDetailItem.setConditionId(responseObject.getInt(SearchDetailContent.CONDITION_ID));
        mSearchDetailItem.setOfferTypeId(responseObject.getInt(SearchDetailContent.OFFER_TYPE_ID));
        mSearchDetailItem.setLocalizationTypeId(responseObject.getInt(SearchDetailContent.LOCALIZATION_TYPE_ID));
        mSearchDetailItem.setStateId(responseObject.getInt(SearchDetailContent.STATE_ID));
        mSearchDetailItem.setCity(responseObject.getString(SearchDetailContent.CITY));
        mSearchDetailItem.setPostCode(responseObject.getString(SearchDetailContent.POST_CODE));
        mSearchDetailItem.setDistance(responseObject.getInt(SearchDetailContent.DISTANCE_ID));

        List<SearchDetailContent.ChosenCategory> choosenCategoryList = new ArrayList<>();
        JSONArray categories = new JSONArray(responseObject.getString(SearchDetailContent.CATEGORIES));

        if (categories!=null) {
            for (int i = 0; i < categories.length(); i++) {
                JSONObject ob = categories.getJSONObject(i);
                SearchDetailContent.ChosenCategory choosenCategory = new SearchDetailContent.ChosenCategory();
                List<CategoryContent.CategoryItem> siblingCategories = new ArrayList<>();
                choosenCategory.setCategoryId(ob.getInt(SearchDetailContent.SELECTED_CATEGORY_ID));
                JSONArray siblings = ob.getJSONArray(SearchDetailContent.CATEGORIES);

                for (int j = 0; j < siblings.length(); j++) {
                    JSONObject jsonCategory = siblings.getJSONObject(j);
                    CategoryContent.CategoryItem categoryItem = new CategoryContent.CategoryItem(
                            String.valueOf(jsonCategory.getInt(CategoryContent.ID)),
                            jsonCategory.getString(CategoryContent.NAME));

                    if (choosenCategory.getCategoryId().toString().equals(categoryItem.getId()))
                        choosenCategory.setCategoryPositionOnSiblings(j);
                    siblingCategories.add(categoryItem);

                }
                choosenCategory.setSiblings(siblingCategories);
                choosenCategoryList.add(choosenCategory);
            }
        }
        mSearchDetailItem.setSelectedCategories(choosenCategoryList);

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //TODO: Zrobić jakąś jedną klasę do wykonywania zapytań żeby nie powtarzać wszędzie tego samego kodu i potem tylko parsować wyniki

    /**
     * Klasa odpowiedzialna za asynchroniczne pobranie kategorii.
     */
    public class FetchCategoryTask extends AsyncTask<Void, Void, List<CategoryContent.CategoryItem>> {

        private String LOG_TAG = FetchCategoryTask.class.getSimpleName();
        private String categoryId;
        private List<CategoryContent.CategoryItem> list;

        public FetchCategoryTask(String categoryId, List<CategoryContent.CategoryItem> list){
            this.categoryId = categoryId;
            this.list = list;
        }

        @Override
        protected  List<CategoryContent.CategoryItem> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Contain raw JSON response
            String responseJsonStr = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("webapi.allenotify.pl")
                        .appendPath("Database/ChildrenOfCategory")
                        .appendPath(categoryId);


                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
                        .getApplicationContext());
                String token = sharedPrefs.getString(getActivity().getApplicationContext()
                        .getString(R.string.prefs_access_token_key), "");
                Log.v(LOG_TAG, "TOKEN:" + token);
                urlConnection.setRequestProperty("Authorization", "bearer " + token);

                urlConnection.connect();
                Log.v(LOG_TAG, "RESPONSE CODE: " + String.valueOf(urlConnection.getResponseCode()));
                Log.v(LOG_TAG, "RESPONSE MESSAGE: " + urlConnection.getResponseMessage());

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return null;
                }

                String line;
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                responseJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Category response: " + responseJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR! No connection?", e);
                return null;
            }finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null)
                    try { reader.close();} catch (IOException e) { Log.e(LOG_TAG, "Error: Cannot close reader", e); }
            }

            try {
                return getCategoriesFromJson(responseJsonStr);
            } catch (JSONException | ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<CategoryContent.CategoryItem> categoryItems) {
            addCategorySpinner(categoryItems, categoryId);
        }

        /**
         * Konwertuje odpowiedź usługi na listę kategorii.
         * @param jsonStr
         * @return
         * @throws JSONException
         * @throws ParseException
         */
        private  List<CategoryContent.CategoryItem> getCategoriesFromJson(String jsonStr)
                throws JSONException, ParseException {
            JSONArray itemsArray = new JSONArray(jsonStr);
            List<CategoryContent.CategoryItem> result = new ArrayList<>(itemsArray.length()+1);

            for (int i=0; i < itemsArray.length(); i++){
                JSONObject ob = itemsArray.getJSONObject(i);
                CategoryContent.CategoryItem item = new CategoryContent.CategoryItem(
                        String.valueOf(ob.getInt(CategoryContent.ID)),
                        ob.getString(CategoryContent.NAME));
                result.add(item);
            }

            list.clear();
            list.addAll(result);
            return result;

        }

    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        boolean userSelect = false;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect)
                chooseCategory((Spinner) parent, id);
            userSelect = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(getActivity(), "Noting selected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }
    }
}
