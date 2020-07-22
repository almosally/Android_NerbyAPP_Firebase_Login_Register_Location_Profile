package com.fontys.android.andr2;

import android.app.Application;

public class MyApplication extends Application {
    private static boolean activityVisible;
    private static boolean activityDestroyed;

    public static boolean isActivityDestroyed() {
        return activityDestroyed;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityDestroyed = false;
        activityVisible = true;
    }

    public static void activityPaused() {
        activityDestroyed = false;
        activityVisible = false;
    }

    public static void activityDestroyed() {
        activityDestroyed = true;
        activityVisible = false;
    }
}
