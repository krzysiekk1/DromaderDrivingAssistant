package com.skobbler.sdkdemo.activity;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.skobbler.debugkit.activity.DebugMapActivity;
import com.skobbler.debugkit.util.DebugKitConfig;
import com.skobbler.ngx.SKDeveloperKeyException;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.SKMapsInitializationListener;

import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.ngx.versioning.SKMapVersioningListener;
import com.skobbler.ngx.versioning.SKVersioningManager;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.application.ApplicationPreferences;
import com.skobbler.sdkdemo.application.DDAApplication;
import com.skobbler.sdkdemo.util.Utils;

/**
 * Activity that installs required resources (from assets/MapResources.zip) to
 * the device
 */
public class SplashActivity extends Activity implements SKMapsInitializationListener, SKMapVersioningListener {

    private static final String TAG = "SplashActivity";
    public static int newMapVersionDetected = 0;

    private boolean update = false;
    private long startLibInitTime;
    /**
     * flag that shows whether the debug kit is enabled or not
     */
    private boolean debugKitEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SKLogging.enableLogs(true);
        boolean multipleMapSupport = false;

        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            multipleMapSupport = bundle.getBoolean("provideMultipleMapSupport");
            debugKitEnabled = bundle.getBoolean(DebugKitConfig.ENABLE_DEBUG_KIT_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            debugKitEnabled = false;
            e.printStackTrace();
        }
        if (multipleMapSupport) {
            SKMapSurfaceView.preserveGLContext = false;
            Utils.isMultipleMapSupportEnabled = true;
        }

        try {
            SKLogging.writeLog(TAG, "Initialize SKMaps", SKLogging.LOG_DEBUG);
            startLibInitTime = System.currentTimeMillis();
            checkForSDKUpdate();
//            SKMapsInitSettings mapsInitSettings = new SKMapsInitSettings();
//            mapsInitSettings.setMapResourcesPath(getExternalFilesDir(null).toString()+"/SKMaps/");
          //  mapsInitSettings.setConnectivityMode(SKMaps.CONNECTIVITY_MODE_OFFLINE);
          //  mapsInitSettings.setPreinstalledMapsPath(getExternalFilesDir(null).toString()+"/SKMaps/PreinstalledMaps/");
            SKMaps.getInstance().initializeSKMaps(getApplication(), this);
        } catch (SKDeveloperKeyException exception) {
            exception.printStackTrace();
            Utils.showApiKeyErrorDialog(this);
        }

    }


    @Override
    public void onLibraryInitialized(boolean isSuccessful) {
        SKLogging.writeLog(TAG, " SKMaps library initialized isSuccessful= " + isSuccessful + " time= " + (System.currentTimeMillis() - startLibInitTime), SKLogging.LOG_DEBUG);
        if (isSuccessful) {
            final DDAApplication app = (DDAApplication) getApplication();
            app.setMapCreatorFilePath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath() + "MapCreator/mapcreatorFile.json");
            app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
            copyOtherResources();
            prepareMapCreatorFile();
            //everything ok. proceed
            SKVersioningManager.getInstance().setMapUpdateListener(this);
            goToMap();
        } else {
            //map was not initialized successfully
            finish();
        }
    }
    private void goToMap() {
        finish();
        if (!debugKitEnabled) {
            startActivity(new Intent(this, MapActivity.class));
        } else {
            Intent intent = new Intent(this, DebugMapActivity.class);
            intent.putExtra("mapResourcesPath", SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
            startActivity(intent);
        }
    }
    /**
     * Copy some additional resources from assets
     */
    private void copyOtherResources() {
        final String mapResourcesDirPath = SKMaps.getInstance().getMapInitSettings().getMapResourcesPath();
        new Thread() {

            public void run() {
                try {
                    boolean resAlreadyExist;
                    String imagesPath = mapResourcesDirPath + "images";
                    File imagesDir = new File(imagesPath);
                    resAlreadyExist = imagesDir.exists();
                    if (!resAlreadyExist || update) {
                        if (!resAlreadyExist) {
                            imagesDir.mkdirs();
                        }
                        Utils.copyAssetsToFolder(getAssets(), "images", mapResourcesDirPath + "images");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Copies the map creator file and logFile from assets to a storage.
     */
    private void prepareMapCreatorFile() {
        final String mapResourcesDirPath = SKMaps.getInstance().getMapInitSettings().getMapResourcesPath();
        final DDAApplication app = (DDAApplication) getApplication();
        final Thread prepareGPXFileThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    boolean resAlreadyExist;

                    final String mapCreatorFolderPath = mapResourcesDirPath + "MapCreator";
                    // create the folder where you want to copy the json file
                    final File mapCreatorFolder = new File(mapCreatorFolderPath);

                    resAlreadyExist = mapCreatorFolder.exists();
                    if (!resAlreadyExist || update) {
                        if (!resAlreadyExist) {
                            mapCreatorFolder.mkdirs();
                        }
                        app.setMapCreatorFilePath(mapCreatorFolderPath + "/mapcreatorFile.json");
                        Utils.copyAsset(getAssets(), "MapCreator", mapCreatorFolderPath, "mapcreatorFile.json");
                    }

                 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        prepareGPXFileThread.start();
    }


    /**
     * Checks if the current version code is grater than the previous and performs an SDK update.
     */
    public void checkForSDKUpdate() {
        DDAApplication appContext = (DDAApplication) getApplication();
        int currentVersionCode = appContext.getAppPrefs().getIntPreference(ApplicationPreferences.CURRENT_VERSION_CODE);
        int versionCode = getVersionCode();
        if (currentVersionCode == 0) {
            appContext.getAppPrefs().setCurrentVersionCode(versionCode);
        }

        if (0 < currentVersionCode && currentVersionCode < versionCode) {
           SKMaps.getInstance().updateToLatestSDKVersion = true;
            appContext.getAppPrefs().setCurrentVersionCode(versionCode);
        }
    }

    /**
     * Returns the current version code
     *
     * @return
     */
    public int getVersionCode() {
        int v = 0;
        try {
            v = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }

    @Override
    public void onNewVersionDetected(int i) {
        Log.e(""," New version = " + i);
        newMapVersionDetected = i;

    }

    @Override
    public void onMapVersionSet(int i) {

    }

    @Override
    public void onVersionFileDownloadTimeout() {

    }

    @Override
    public void onNoNewVersionDetected() {

    }
}
