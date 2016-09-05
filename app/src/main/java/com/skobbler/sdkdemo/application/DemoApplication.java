package com.skobbler.sdkdemo.application;


import android.app.Application;

/**
 * Class that stores global application state
 */
public class DemoApplication extends Application {

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
