package pl.allenotify.anotify.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.allenotify.anotify.R;
import pl.allenotify.anotify.model.UserSearchContent;

/**
 * Created by marcin on 03.04.16.
 * Task odpowiedzialny za pobranie listy wyszukiwań użytkownika i zwrócenie jej w wygodnej postaci.
 */
public class FetchItemList extends AsyncTask<Void, Void, List<UserSearchContent.UserSearchItem>> {

    private String LOG_TAG = FetchItemList.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView.Adapter mListAdapter = null;
    private final String dateFormat = "yyyy-mm-dd'T'HH:mm:ss";
    private final String stringDateFormat = "dd-mm-yyyy";

    public FetchItemList(Context c, RecyclerView.Adapter adapter){
        mContext = c;
        this.mListAdapter = adapter;
    }

    @Override
    protected List<UserSearchContent.UserSearchItem> doInBackground(Void... params) {
/*        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;*/
        //Contain raw JSON response
        String responseJsonStr = null;
        OkHttpClient client = new OkHttpClient();
        try {

            Request.Builder builder = new Request.Builder();
            URL url = new URL("http://webapi.allenotify.pl/SearchItem");
           /* urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");*/
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String token = sharedPrefs.getString(mContext.getString(R.string.prefs_access_token_key),"");
            //Log.v(LOG_TAG, "TOKEN:" + token);
/*
            urlConnection.setRequestProperty("Authorization", "bearer " + token);
*/

            builder.url(url).addHeader("Accept", "application/json")
                    .addHeader("Authorization", "bearer " + token );
            Request request = builder.build();

            /*urlConnection.connect();*/
/*            Log.v(LOG_TAG, "RESPONSE CODE: " + String.valueOf(urlConnection.getResponseCode()));
            Log.v(LOG_TAG, "RESPONSE MESSAGE: " + urlConnection.getResponseMessage());*/

/*            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }*/

            Response response = client.newCall(request).execute();
            Log.v(LOG_TAG, "RESPONSE CODE: " + response.code());

            if (response.code() != HttpURLConnection.HTTP_OK){
                return null;
            }

            String line;
            StringBuffer buffer = new StringBuffer();

            responseJsonStr = response.body().string();

/*            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            responseJsonStr = buffer.toString();*/

            Log.v(LOG_TAG, "RESPONSE BODY: " + responseJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR! No connection?", e);
            return null;
        }finally {
/*            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null)
                try { reader.close();} catch (IOException e) { Log.e(LOG_TAG, "Error: Cannot close reader", e); }*/
            mContext = null;
        }

        try {
            return getDataFromJson(responseJsonStr);
        } catch (JSONException | ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    /**
     * Powiadamia adapter listy o zmianach.
     * @param userSearchItems
     */
    @Override
    protected void onPostExecute(List<UserSearchContent.UserSearchItem> userSearchItems) {
        mListAdapter.notifyDataSetChanged();
        mListAdapter = null;
    }

    /**
     * Przetwarza odpowiedź i dodaje przedmioty do listy
     * @param jsonStr
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private List<UserSearchContent.UserSearchItem> getDataFromJson(String jsonStr)
            throws JSONException, ParseException {
        JSONArray itemsArray = new JSONArray(jsonStr);
        List<UserSearchContent.UserSearchItem> result = new ArrayList<>(itemsArray.length());
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        DateFormat stringDf = new SimpleDateFormat(this.stringDateFormat);

        for (int i=0; i < itemsArray.length(); i++){
            JSONObject ob = itemsArray.getJSONObject(i);
            //Change received date String to Date object
            Date date = df.parse(ob.getString(UserSearchContent.DATE));
            //Fomat the Date object into String to display it on view
            String dateString = stringDf.format(date);
            UserSearchContent.UserSearchItem item = new UserSearchContent.UserSearchItem(String.valueOf(ob.getInt(UserSearchContent.ID)),
                    ob.getString(UserSearchContent.NAME),
                    String.valueOf(ob.getInt(UserSearchContent.COUNT_FOUND)), dateString,
                    String.valueOf(ob.getInt(UserSearchContent.STATUS_ID)));
            result.add(item);
        }

        UserSearchContent.items.clear();
        UserSearchContent.items.addAll(result);
        return result;

    }

    @Override
    protected void onCancelled() {
        mContext = null;
        mListAdapter = null;
    }
}
