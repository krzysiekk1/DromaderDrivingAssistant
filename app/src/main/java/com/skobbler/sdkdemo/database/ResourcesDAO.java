package com.skobbler.sdkdemo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.skobbler.ngx.util.SKLogging;

/**
 * Class responsible for creating and upgrading the application's database.
 * Created by CatalinM on 11/10/2014.
 */
public class ResourcesDAO extends SQLiteOpenHelper {

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
        SKLogging.writeLog(TAG, "On create resources database !!!", SKLogging.LOG_DEBUG);
        String createMapResourcesTable =
                new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(MapsDAO.MAPS_TABLE).append(" (")
                        .append(MapsDAO.KEY).append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "").append(MapsDAO.CODE).append(" TEXT UNIQUE, ").append(MapsDAO.PARENT_CODE)
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
        db.beginTransaction();
        db.execSQL(createMapResourcesTable);
        db.setTransactionSuccessful();
        db.endTransaction();
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