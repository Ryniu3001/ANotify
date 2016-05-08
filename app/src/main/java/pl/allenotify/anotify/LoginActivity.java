package pl.allenotify.anotify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity logowania użytkownika.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton == null) throw new AssertionError();
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null){
            return;
        }
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mEmailView.setError(null);
        mPasswordView.setError(null);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(mEmailView.getText().toString(), mPasswordView.getText().toString());
            mAuthTask.execute();
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isEmailValid(String email) {
        //TODO: add more logic
        return email.contains("@");
    }

    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Map<String, String>> {

        private final String LOG_TAG = UserLoginTask.class.getSimpleName();
        private final String mEmail;
        private final String mPassword;

        // These are the names of the JSON objects that need to be extracted.
        private final String ACCESS_TOKEN = "access_token";
        private final String USER_NAME = "userName";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            JSONObject credentials = new JSONObject();

            try {
                credentials.put("Password", mPassword);
                credentials.put("Email", mEmail);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error!!", e);
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            OutputStreamWriter wr = null;

            //Contain raw JSON response
            String responseJsonStr = null;

            try {
                URL url = new URL("http://webapi.allenotify.pl/account/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(credentials.toString());
                wr.flush();
                urlConnection.connect();
                Log.v(LOG_TAG, "REQUEST: " + wr.toString());

                Log.v(LOG_TAG, "RESPONSE CODE: " + String.valueOf(urlConnection.getResponseCode()));
                Log.v(LOG_TAG, "RESPONSE MESSAGE: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    urlConnection.getResponseMessage();
                    return null;
                }

                String line;
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                responseJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Login response: " + responseJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR! No connection?", e);
                return null;
            }finally {
                if (wr != null)
                    try { wr.close(); } catch (IOException e) { Log.e(LOG_TAG, "Error: Cannot close wr", e); }
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null)
                    try { reader.close();} catch (IOException e) { Log.e(LOG_TAG, "Error: Cannot close reader", e); }
            }

            try {
                return getDataFromJson(responseJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Map<String, String> responseMap) {
            mAuthTask = null;
            showProgress(false);

            if ( responseMap != null && responseMap.size() > 0) {
                //Show message and save user data to sharedPrefs
                Toast.makeText(getApplicationContext(), "LOGGED IN!", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPrefs.edit().putString(getString(R.string.prefs_access_token_key), responseMap.get(ACCESS_TOKEN)).apply();
                sharedPrefs.edit().putString(getString(R.string.prefs_user_name_key), responseMap.get(USER_NAME)).apply();
                //Call MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish(); //LoginActivity won't be needed
                startActivity(intent);

            } else {

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);
                dlgAlert.setMessage(getString(R.string.error_invalid_username_password));
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.create().show();
               // mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private Map<String, String> getDataFromJson(String jsonStr)
                throws JSONException {
            JSONObject loginJson = new JSONObject(jsonStr);

            Map<String, String> result = new HashMap<>();
            result.put(ACCESS_TOKEN, loginJson.getString(ACCESS_TOKEN));
            //result.put(TOKEN_TYPE, loginJson.getString(TOKEN_TYPE));
            //result.put(EXPIRES_IN, String.valueOf(loginJson.getInt(EXPIRES_IN)));
            result.put(USER_NAME, loginJson.getString(USER_NAME));
            //result.put(ISSUED, loginJson.getString(ISSUED));
            //result.put(EXPIRES, loginJson.getString(EXPIRES));

            return result;

        }
    }
}
