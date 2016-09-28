package com.skobbler.sdkdemo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.skobbler.ngx.util.SKLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class responsible for creating and upgrading the application's database.
 */
public class ResourcesDAO extends SQLiteOpenHelper {

    private Context context;

    /**
     * the name of the database
     */
    public static final String DATABASE_NAME = "application_database";

    /**
     * the database version
     */
    public static final byte DATABASE_VERSION = 1;

    /**
     * tag associated with current class
     */
    private static final String TAG = "ResourcesDAO";

    /**
     * an instance of this class
     */
    private static ResourcesDAO databaseInstance;

    /**
     * SQLITE database instance
     */
    private SQLiteDatabase sqLiteDatabaseInstance;

    /**
     * creates a new ResourcesDAO object
     * @param context the context of the application
     */
    private ResourcesDAO(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Returns the {@code instance}
     * @param context the context of the application
     * @return the instance of the class
     */
    public static ResourcesDAO getInstance(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = new ResourcesDAO(context);
        }
        return databaseInstance;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        SKLogging.writeLog("TollsCostCalculator", "jest git", 0);
        SKLogging.writeLog(TAG, "On create resources database !!!", SKLogging.LOG_DEBUG);

        db.beginTransaction();
        db.execSQL(createMapResourcesTable());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        db.execSQL(createVignetteHighwaysTable());
        db.setTransactionSuccessful();
        db.endTransaction();

        InputStream is = context.getResources().openRawResource(com.skobbler.ngx.R.raw.vignette_highways);
        InputStreamReader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r);
        String line = null;
        try {
            line = br.readLine();
            db.beginTransaction();
            line = br.readLine();
            while (line != null) {
                String[] values = line.split(",");
                db.execSQL(fillVignetteHighwaysTable(values));
                line = br.readLine();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createMapResourcesTable() {
        String createMapResourcesTable =
                new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(MapsDAO.MAPS_TABLE).append(" (")
                        .append(MapsDAO.KEY).append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "").append(MapsDAO.CODE).append(" TEXT, ").append(MapsDAO.PARENT_CODE)
                        .append(" TEXT, ").append(MapsDAO.REGION).append(" TEXT, ").append(MapsDAO.NAMES).append(" TEXT, " +
                        "").append(MapsDAO.SKM_FILE_PATH).append(" TEXT, ").append(MapsDAO.ZIP_FILE_PATH)
                        .append(" TEXT, ").append(MapsDAO.TXG_FILE_PATH).append(" TEXT, ")
                        .append(MapsDAO.TXG_FILE_SIZE).append(" INTEGER, " +
                        "").append(MapsDAO.SKM_AND_ZIP_FILES_SIZE)
                        .append(" INTEGER, ").append(MapsDAO.SKM_FILE_SIZE).append(" INTEGER, " +
                        "").append(MapsDAO.UNZIPPED_FILE_SIZE)
                        .append(" INTEGER, ").append(MapsDAO.BOUNDING_BOX_LATITUDE_MAX).append(" DOUBLE, ")
                        .append(MapsDAO.BOUNDING_BOX_LATITUDE_MIN).append(" DOUBLE, ")
                        .append(MapsDAO.BOUNDING_BOX_LONGITUDE_MAX).append(" DOUBLE, ")
                        .append(MapsDAO.BOUNDING_BOX_LONGITUDE_MIN).append(" DOUBLE, " +
                        "").append(MapsDAO.SUBTYPE)
                        .append(" TEXT, ").append(MapsDAO.STATE).append(" INTEGER, ")
                        .append(MapsDAO.NO_DOWNLOADED_BYTES).append(" INTEGER, ").append(MapsDAO.FLAG_ID)
                        .append(" INTEGER, ").append(MapsDAO.DOWNLOAD_PATH).append(" TEXT)").toString();
        return createMapResourcesTable;
    }

    public String createVignetteHighwaysTable() {
        String create = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append("VignetteHighways").append(" (")
                .append("Id").append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ").append("RoadNr")
                .append(" TEXT, ").append("CountryCode").append(" TEXT)").toString();
        return create;
    }

    public String fillVignetteHighwaysTable(String[] values) {
        int id = Integer.parseInt(values[0].substring(1, values[0].length()-1));
        String road_nr = values[1];
        String country_code = values[2];
        String fill = new StringBuilder("INSERT INTO ").append("VignetteHighways").append(" VALUES(")
                .append(id).append(", ").append(road_nr).append(", ").append(country_code).append(")").toString();
        return fill;
    }

    /**
     * Returns the {@code database}
     * @return the database
     */
    public SQLiteDatabase getDatabase() {
        return sqLiteDatabaseInstance;
    }

    /**
     * Opens the application database.
     */
    public void openDatabase() {
        try {
            if ((sqLiteDatabaseInstance == null) || !sqLiteDatabaseInstance.isOpen()) {
                sqLiteDatabaseInstance = getWritableDatabase();
            }
        } catch (final SQLException e) {
            SKLogging.writeLog(TAG, "Error when opening database: " + e.getMessage(), SKLogging.LOG_WARNING);
            sqLiteDatabaseInstance = getReadableDatabase();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}