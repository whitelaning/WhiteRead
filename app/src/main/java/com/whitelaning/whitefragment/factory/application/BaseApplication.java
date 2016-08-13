package com.whitelaning.whitefragment.factory.application;

import android.app.Application;

import com.whitelaning.whitefragment.factory.console.SwitchTool;
import com.whitelaning.whitefragment.thirdparty.logger.LogLevel;
import com.whitelaning.whitefragment.thirdparty.logger.Logger;
import com.whitelaning.whitefragment.thirdparty.logger.Settings;

/**
 * Created by Zack White on 2016/5/17.
 */
public class BaseApplication extends Application {

    private static final String MY_TAG = "WhiteFragment";
    private static boolean isFirst = true;

    private static class uniqueInstance {
        private static BaseApplication instance;
    }

    public static BaseApplication getInstance() {
        return uniqueInstance.instance;
    }

    public static BaseApplication getContext() {
        return getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isFirst) {
            isFirst = false;
            uniqueInstance.instance = this;
            initLogger();
            initData();
        }
    }

    protected void initData(){

    }

    private void initLogger() {

        /*
        Logger
            .init(MY_TAG)                   // default PRETTYLOGGER or use just init()
            .methodCount(2)                 // default 2
            .hideThreadInfo()               // default shown
            .logLevel(LogLevel.NONE)        // default LogLevel.FULL
            .methodOffset(2)                // default 0
            .logTool(new AndroidLogTool()); // custom log tool, optional
        */

        Settings mLoggerSettings = Logger.init(MY_TAG);
        mLoggerSettings.methodCount(1);
        mLoggerSettings.hideThreadInfo();
        if (SwitchTool.isDebug) {
            mLoggerSettings.logLevel(LogLevel.FULL);
        } else {
            mLoggerSettings.logLevel(LogLevel.NONE);
        }
    }
}

