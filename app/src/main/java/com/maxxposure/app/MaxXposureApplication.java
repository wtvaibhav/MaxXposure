package com.maxxposure.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.FileUtils;

public class MaxXposureApplication extends MultiDexApplication {

    public static Context appContext;

    public static final String CHANNEL_ID = "trackinglocation";

    public static int SC_WIDTH=0;
    public static int SC_HEIGHT=0;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        createNotificationChannel();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                try {
                    String error = Log.getStackTraceString(ex);
                    FileUtils.saveLogs(error);
                    System.exit(1);

                } catch (Exception e) {
                    FileUtils.saveLogs(e.getMessage());
                }
            }
        });
    }

    public static synchronized MaxXposureApplication getInstance() {
        return (MaxXposureApplication) appContext;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tracking Location",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
