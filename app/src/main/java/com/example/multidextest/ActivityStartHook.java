package com.example.multidextest;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityStartHook {
    private static final String TAG = "HookStartActivity";

    public static void hook(Context context) {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method method = activityThreadClass.getDeclaredMethod("currentActivityThread");
            method.setAccessible(true);
            Object activityThread = method.invoke(null);
            Log.d(TAG, activityThread.toString());
            Field instrumentation = activityThreadClass.getDeclaredField("mInstrumentation");
            instrumentation.setAccessible(true);
            Instrumentation old = (Instrumentation) instrumentation.get(activityThread);
            Log.d(TAG, old.toString());
            instrumentation.set(activityThread, new ProxyInstrumentation(context, old));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static class ProxyInstrumentation extends Instrumentation {
        private Context context;
        private Instrumentation old;

        public ProxyInstrumentation(Context context, Instrumentation old) {
            this.context = context;
            this.old = old;
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle) {
            super.callActivityOnCreate(activity, icicle);
        }

        public ActivityResult execStartActivity(
                Context who, final IBinder contextThread, final IBinder token, final Activity target,
                final Intent intent, final int requestCode, final Bundle options) {
            if (!MyApplication.sInstalled) {
                final ProgressDialog progressDialog = new ProgressDialog(who);
                progressDialog.show();
                final Context myContext = who;
                MyApplication.getInstance().registerListener(new MyApplication.InstallListener() {
                    @Override
                    public void onInstallSuccess() {
                        try {
                            progressDialog.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        execStartActivity(myContext, contextThread, token, target, intent, requestCode, options);
                    }
                });
                return null;
            }
            try {
                Method method = Instrumentation.class.getDeclaredMethod("execStartActivity",
                        Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
                return (ActivityResult) method.invoke(old, who, contextThread, token, target, intent, requestCode, options);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
