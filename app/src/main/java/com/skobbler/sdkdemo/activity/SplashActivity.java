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
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.skobbler.debugkit.activity.DebugMapActivity;
import com.skobbler.debugkit.util.DebugKitConfig;
import com.skobbler.ngx.SKPrepareMapTextureListener;
import com.skobbler.ngx.SKPrepareMapTextureThread;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.ngx.versioning.SKMapUpdateListener;
import com.skobbler.ngx.versioning.SKVersioningManager;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.application.ApplicationPreferences;
import com.skobbler.sdkdemo.application.DemoApplication;
import com.skobbler.sdkdemo.util.DemoUtils;

/**
 * Activity that installs required resources (from assets/MapResources.zip) to
 * the device
 */
public class SplashActivity extends Activity implements SKPrepareMapTextureListener, SKMapUpdateListener {

    /**
     * Path to the MapResources directory
     */
    public static String mapResourcesDirPath = "";

    private boolean update = false;

    private static final String TAG = "SplashActivity";
    public static int newMapVersionDetected = 0;

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
        if (multipleMapSupport){
            SKMapSurfaceView.preserveGLContext = false;
            DemoUtils.isMultipleMapSupportEnabled = true;
        }

        String applicationPath = chooseStoragePath(this);

        // determine path where map resources should be copied on the device
        if (applicationPath != null) {
            mapResourcesDirPath = applicationPath + "/" + "SKMaps/";
        } else {
            // show a dialog and then finish
        }
        ((DemoApplication)getApplicationContext()).getAppPrefs().saveStringPreference("mapResourcesPath", mapResourcesDirPath);
        ((DemoApplication) getApplication()).setMapResourcesDirPath(mapResourcesDirPath);
        checkForUpdate();
        if (!new File(mapResourcesDirPath).exists()) {
            // copy some other resource needed
            new SKPrepareMapTextureThread(this, mapResourcesDirPath, "SKMaps.zip", this).start();
            copyOtherResources();
            prepareMapCreatorFile();
        } else if(!update){
            final DemoApplication app = (DemoApplication) getApplication();
            app.setMapCreatorFilePath(mapResourcesDirPath + "MapCreator/mapcreatorFile.json");
            Toast.makeText(SplashActivity.this, "Map resources copied in a previous run", Toast.LENGTH_SHORT).show();
            prepareMapCreatorFile();
            DemoUtils.initializeLibrary(this);
            SKVersioningManager.getInstance().setMapUpdateListener(this);
            goToMap();
        }

    }

    @Override
    public void onMapTexturesPrepared(boolean prepared) {

        SKVersioningManager.getInstance().setMapUpdateListener(this);
        Toast.makeText(SplashActivity.this, "Map resources were copied", Toast.LENGTH_SHORT).show();

        if (DemoUtils.initializeLibrary(this)) {
            goToMap();
        }
    }

    private void goToMap() {
        finish();
        if (!debugKitEnabled) {
            startActivity(new Intent(this, MapActivity.class));
        } else {
            Intent intent = new Intent(this, DebugMapActivity.class);
            intent.putExtra("mapResourcesPath", mapResourcesDirPath);
            startActivity(intent);
        }
    }

    /**
     * Copy some additional resources from assets
     */
    private void copyOtherResources() {
        new Thread() {

            public void run() {
                try {
                    String tracksPath = mapResourcesDirPath + "GPXTracks";
                    File tracksDir = new File(tracksPath);
                    if (!tracksDir.exists()) {
                        tracksDir.mkdirs();
                    }
                    DemoUtils.copyAssetsToFolder(getAssets(), "GPXTracks", mapResourcesDirPath + "GPXTracks");

                    String imagesPath = mapResourcesDirPath + "images";
                    File imagesDir = new File(imagesPath);
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs();
                    }
                    DemoUtils.copyAssetsToFolder(getAssets(), "images", mapResourcesDirPath + "images");
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
        final DemoApplication app = (DemoApplication) getApplication();
        final Thread prepareGPXFileThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    final String mapCreatorFolderPath = mapResourcesDirPath + "MapCreator";
                    final File mapCreatorFolder = new File(mapCreatorFolderPath);
                    // create the folder where you want to copy the json file
                    if (!mapCreatorFolder.exists()) {
                        mapCreatorFolder.mkdirs();
                    }
                    app.setMapCreatorFilePath(mapCreatorFolderPath + "/mapcreatorFile.json");
                    DemoUtils.copyAsset(getAssets(), "MapCreator", mapCreatorFolderPath, "mapcreatorFile.json");
                    // Copies the log file from assets to a storage.
                    final String logFolderPath = mapResourcesDirPath + "logFile";
                    final File logFolder = new File(logFolderPath);
                    if (!logFolder.exists()) {
                        logFolder.mkdirs();
                    }
                    DemoUtils.copyAsset(getAssets(), "logFile", logFolderPath, "Seattle.log");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        prepareGPXFileThread.start();
    }


    @Override
    public void onMapVersionSet(int newVersion) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNewVersionDetected(int newVersion) {
        // TODO Auto-generated method stub
        Log.e("", "new version " + newVersion);
        newMapVersionDetected = newVersion;
    }

    @Override
    public void onNoNewVersionDetected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVersionFileDownloadTimeout() {
        // TODO Auto-generated method stub

    }

    public static final long KILO = 1024;

    public static final long MEGA = KILO * KILO;

    public static String chooseStoragePath(Context context) {
        if (getAvailableMemorySize(Environment.getDataDirectory().getPath()) >= 50 * MEGA) {
            if (context != null && context.getFilesDir() != null) {
                return context.getFilesDir().getPath();
            }
        } else {
            if ((context != null) && (context.getExternalFilesDir(null) != null)) {
                if (getAvailableMemorySize(context.getExternalFilesDir(null).toString()) >= 50 * MEGA) {
                    return context.getExternalFilesDir(null).toString();
                }
            }
        }

        SKLogging.writeLog(TAG, "There is not enough memory on any storage, but return internal memory",
                SKLogging.LOG_DEBUG);

        if (context != null && context.getFilesDir() != null) {
            return context.getFilesDir().getPath();
        } else {
            if ((context != null) && (context.getExternalFilesDir(null) != null)) {
                return context.getExternalFilesDir(null).toString();
            } else {
                return null;
            }
        }
    }
    /**
    * Checks if the current version code is grater than the previous and overwrites the map resources.
    */
    public void checkForUpdate(){
        DemoApplication appContext = (DemoApplication) getApplication();
        int currentVersionCode = appContext.getAppPrefs().getIntPreference(ApplicationPreferences.CURRENT_VERSION_CODE);
        int versionCode = getVersionCode();
        if(currentVersionCode == 0){
            appContext.getAppPrefs().setCurrentVersionCode(versionCode);
        }

        if( 0 < currentVersionCode && currentVersionCode < versionCode){
            update = true;
            appContext.getAppPrefs().setCurrentVersionCode(versionCode);
            DemoUtils.deleteFileOrDirectory(new File(mapResourcesDirPath));
            new SKPrepareMapTextureThread(this, mapResourcesDirPath, "SKMaps.zip", this).start();
            copyOtherResources();
            prepareMapCreatorFile();
        }

    }

    /**
     * get the available internal memory size
     *
     * @return available memory size in bytes
     */
    public static long getAvailableMemorySize(String path) {
        StatFs statFs = null;
        try {
            statFs = new StatFs(path);
        } catch (IllegalArgumentException ex) {
            SKLogging.writeLog("SplashActivity", "Exception when creating StatF ; message = " + ex,
                    SKLogging.LOG_DEBUG);
        }
        if (statFs != null) {
            Method getAvailableBytesMethod = null;
            try {
                getAvailableBytesMethod = statFs.getClass().getMethod("getAvailableBytes");
            } catch (NoSuchMethodException e) {
                SKLogging.writeLog(TAG, "Exception at getAvailableMemorySize method = " + e.getMessage(),
                        SKLogging.LOG_DEBUG);
            }

            if (getAvailableBytesMethod != null) {
                try {
                    SKLogging.writeLog(TAG, "Using new API for getAvailableMemorySize method !!!", SKLogging.LOG_DEBUG);
                    return (Long) getAvailableBytesMethod.invoke(statFs);
                } catch (IllegalAccessException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                } catch (InvocationTargetException e) {
                    return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                }
            } else {
                return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
            }
        } else {
            return 0;
        }
    }

    /**
     * Returns the current version code
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
}
