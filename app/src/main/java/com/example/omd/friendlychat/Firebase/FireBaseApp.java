package com.example.omd.friendlychat.Firebase;

import android.app.Application;
import com.firebase.client.Firebase;

/**
 * Created by Delta on 29/04/2017.
 */

public class FireBaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
