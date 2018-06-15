package com.tbs.tbssms.common;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;


public class BaseApplication extends Application
{

    private static BaseApplication mInstance;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        mDaemonClient = new DaemonClient(createDaemonConfigurations());
//        mDaemonClient.onAttachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Thread.setDefaultUncaughtExceptionHandler(AppException
                .getAppExceptionHandler());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }
}
