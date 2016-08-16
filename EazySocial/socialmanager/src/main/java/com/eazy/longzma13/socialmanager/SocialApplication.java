package com.eazy.longzma13.socialmanager;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by Harry on 8/16/16.
 */

public class SocialApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
