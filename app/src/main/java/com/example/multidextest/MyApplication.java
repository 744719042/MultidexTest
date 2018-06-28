package com.example.multidextest;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        HookStartActivity.hook(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
