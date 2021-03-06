package com.skobbler.sdkdemo.application;

import android.app.Application;
import android.content.Context;

/**
 * Class that stores global application state
 */
public class DDAApplication extends Application {

    private static Context context;

    /**
     * Path to the map resources directory on the device
     */
    private String mapResourcesDirPath;

    /**
     * Absolute path to the file used for mapCreator - mapcreatorFile.json
     */
    private String mapCreatorFilePath;

    /**
     * Object for accessing application preferences
     */
    private ApplicationPreferences appPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        appPrefs = new ApplicationPreferences(this);
        DDAApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return DDAApplication.context;
    }

    public void setMapResourcesDirPath(String mapResourcesDirPath) {
        this.mapResourcesDirPath = mapResourcesDirPath;
    }

    public String getMapResourcesDirPath() {
        return mapResourcesDirPath;
    }

    public String getMapCreatorFilePath() {
        return mapCreatorFilePath;
    }

    public void setMapCreatorFilePath(String mapCreatorFilePath) {
        this.mapCreatorFilePath = mapCreatorFilePath;
    }

    public ApplicationPreferences getAppPrefs() {
        return appPrefs;
    }

    public void setAppPrefs(ApplicationPreferences appPrefs) {
        this.appPrefs = appPrefs;
    }
}
