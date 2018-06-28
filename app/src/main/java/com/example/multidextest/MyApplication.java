package com.example.multidextest;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static volatile boolean sInstalled = false;
    private List<InstallListener> mListeners = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static MyApplication sInstance;

    public interface InstallListener {
        void onInstallSuccess();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ActivityStartHook.hook(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultidexInstaller.installPlugins(MyApplication.this);
                    sInstalled = true;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyInstalled();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void registerListener(InstallListener listener) {
        mListeners.add(listener);
    }

    private void notifyInstalled() {
        for (InstallListener listener : mListeners) {
            listener.onInstallSuccess();
        }
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
