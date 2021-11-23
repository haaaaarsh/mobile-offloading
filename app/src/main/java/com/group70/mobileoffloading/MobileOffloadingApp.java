package com.group70.mobileoffloading;

import android.app.Application;

public class MobileOffloadingApp extends Application {

    private static MobileOffloadingApp mInstance;

    public static MobileOffloadingApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

}
