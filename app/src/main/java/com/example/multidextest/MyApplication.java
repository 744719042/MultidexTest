package com.example.multidextest;

import android.app.Application;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultidexInstaller.installPlugins(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
