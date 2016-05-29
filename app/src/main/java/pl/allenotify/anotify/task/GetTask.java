package pl.allenotify.anotify.task;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.allenotify.anotify.LoginActivity;
import pl.allenotify.anotify.R;

/**
 * Created by marcin on 12.05.16.
 */
public class GetTask extends AsyncTask<Void, Void, String> {
    private String LOG_TAG = GetTask.class.getSimpleName();
    public static String PUT = "PUT";
    public static String POST = "POST";
    public static String GET = "GET";

    private GetTaskCaller listener = null;

    private String urlString = null;
    private String method;
    private String jsonBody;
    private int responseCode;

    public GetTask(String url, String method, String jsonBody){
        this.urlString = url;
        this.method = method;
        this.jsonBody = jsonBody;
    }

    public GetTask(String url){
        this.urlString = url;
        this.method = GET;
    }

    @Override
    protected String doInBackground(Void... params) {
        String responseJsonStr = null;
        OkHttpClient client = new OkHttpClient();
        try {

            Request.Builder builder = new Request.Builder();
            URL url = new URL(urlString);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.appContext);
            String token = sharedPrefs.getString(LoginActivity.appContext.getString(R.string.prefs_access_token_key),"");
            if (token == null || token.isEmpty())
                return null;
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            if (method.equals(GET)){
                builder.url(url).addHeader("Accept", "application/json")
                        .addHeader("Authorization", "bearer " + token );
            }else if (method.equals(PUT)){
                RequestBody body = RequestBody.create(JSON, jsonBody);
                builder.url(url).put(body).addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "bearer " + token );
                Log.v(LOG_TAG, "REQUEST " + jsonBody);
            }else if (method.equals(POST)){
                RequestBody body = RequestBody.create(JSON, jsonBody);
                builder.url(url).post(body).addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "bearer " + token );
                Log.v(LOG_TAG, "REQUEST " + jsonBody);
            }

            Request request = builder.build();

            Log.v(LOG_TAG, request.method() + " " + url);
            Response response = client.newCall(request).execute();
            Log.v(LOG_TAG, "RESPONSE CODE: " + response.code());
            Log.v(LOG_TAG, "RESPONSE Message: " + response.message());
            responseJsonStr = response.body().string();

            Log.v(LOG_TAG, "RESPONSE BODY: " + responseJsonStr);

            this.responseCode = response.code();
            if (this.responseCode != HttpURLConnection.HTTP_OK){
                return null;
            }
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
            listener.asyncTaskDone(this.responseCode, s);
    }

    public void registerListener(GetTaskCaller listener){
        this.listener = listener;
    }



    /**
     * callback interface
     */
    public interface GetTaskCaller{
        void asyncTaskDone(int code, String response);
    }
}
