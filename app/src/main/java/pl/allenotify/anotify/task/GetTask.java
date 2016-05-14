package pl.allenotify.anotify.task;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.allenotify.anotify.MainActivity;
import pl.allenotify.anotify.R;

/**
 * Created by marcin on 12.05.16.
 */
public class GetTask extends AsyncTask<String, Void, String> {
    private String LOG_TAG = GetTask.class.getSimpleName();
    private GetTaskCaller listener = null;

    @Override
    protected String doInBackground(String... params) {
        String responseJsonStr = null;
        OkHttpClient client = new OkHttpClient();
        try {

            if (params == null){
                return null;
            }

            Request.Builder builder = new Request.Builder();
            //URL url = new URL("http://webapi.allenotify.pl/SearchItem");
            URL url = new URL(params[0]);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.appContext);
            String token = sharedPrefs.getString(MainActivity.appContext.getString(R.string.prefs_access_token_key),"");


            builder.url(url).addHeader("Accept", "application/json")
                    .addHeader("Authorization", "bearer " + token );
            Request request = builder.build();

            Log.v(LOG_TAG, builder.toString());
            Response response = client.newCall(request).execute();
            Log.v(LOG_TAG, "RESPONSE CODE: " + response.code());

            if (response.code() != HttpURLConnection.HTTP_OK){
                return null;
            }

            String line;
            StringBuffer buffer = new StringBuffer();

            responseJsonStr = response.body().string();

            Log.v(LOG_TAG, "RESPONSE BODY: " + responseJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR! No connection?", e);
            return null;
        }

        return responseJsonStr;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.listener != null)
            listener.asyncTaskDone(s);
    }

    public void registerListener(GetTaskCaller listener){
        this.listener = listener;
    }



    /**
     * callback interface
     */
    public interface GetTaskCaller{
        void asyncTaskDone(String response);
    }
}
