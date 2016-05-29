package pl.allenotify.anotify.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import pl.allenotify.anotify.task.GetTask;

/**
 * Created by marcin on 28.05.16.
 * A service that extends FirebaseInstanceIdService to handle the creation, rotation, and updating
 * of registration tokens. This is required for sending to specific devices or for creating device groups.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Firebase Refresh Token: " + refreshToken);
        if (refreshToken != null)
            sendGoogleTokenToServer(refreshToken);


    }

    public static void sendGoogleTokenToServer(String token){
        token = "\"" + token + "\"";
        GetTask putData = new GetTask("http://webapi.allenotify.pl/api/PushNotificationToken", GetTask.PUT, token);
        putData.execute();
    }
}
