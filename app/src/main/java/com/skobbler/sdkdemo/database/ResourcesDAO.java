package com.skobbler.sdkdemo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class responsible for creating and upgrading the application's database.
 */
public class ResourcesDAO extends SQLiteOpenHelper {

    private Context context;

    /**
     * the name of the database
     */
    private static final String DATABASE_NAME = "application_database";
    /**
     * the database version
     */
    private static final byte DATABASE_VERSION = 1;

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
        SKLogging.writeLog(TAG, "On create resources database !!!", SKLogging.LOG_DEBUG);

        db.beginTransaction();
        db.execSQL(createMapResourcesTable());
        db.setTransactionSuccessful();
        db.endTransaction();

        createTables(db);
        fillTable(db, context.getResources().openRawResource(R.raw.tolls), "tolls");
        fillTable(db, context.getResources().openRawResource(R.raw.vignette_highways), "vignette");
        fillTable(db, context.getResources().openRawResource(R.raw.avg_fuel_costs), "fuel");
    }

    public void updateDatabase (final SQLiteDatabase db, InputStream tollsStream, InputStream vignetteStream, InputStream fuelStream) {
        db.beginTransaction();
        db.execSQL(dropTollsTableQuery());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        db.execSQL(dropVignetteTableQuery());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        db.execSQL(dropFuelTableQuery());
        db.setTransactionSuccessful();
        db.endTransaction();

        createTables(db);
        fillTable(db, tollsStream, "tolls");
        fillTable(db, vignetteStream, "vignette");
        fillTable(db, fuelStream, "fuel");
    }

    private void createTables (final SQLiteDatabase db){
        db.beginTransaction();
        db.execSQL(createTollsTable());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        db.execSQL(createVignetteHighwaysTable());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        db.execSQL(createAvgFuelCostsTable());
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void fillTable(final SQLiteDatabase db, InputStream is, String tableName) {
        InputStreamReader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r);
        String line = null;
        try {
            line = br.readLine();
            db.beginTransaction();
            line = br.readLine();
            switch (tableName) {
                case "tolls":
                    while (line != null) {
                        String[] tollsValues = line.split(",");
                        db.execSQL(fillTollsTable(tollsValues));
                        line = br.readLine();
                    }
                    break;
                case "vignette":
                    while (line != null) {
                        String[] vignetteHighwaysValues = line.split(",");
                        db.execSQL(fillVignetteHighwaysTable(vignetteHighwaysValues));
                        line = br.readLine();
                    }
                    break;
                case "fuel":
                    while (line != null) {
                        String[] avgFuelCosts = line.split(",");
                        db.execSQL(fillAvgFuelCostsTable(avgFuelCosts));
                        line = br.readLine();
                    }
                    break;
                default:
                    break;
            }
                db.setTransactionSuccessful();
                db.endTransaction();
                br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createMapResourcesTable() {
        String createMapResourcesTable =
                new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(MapsDAO.MAPS_TABLE).append(" (").append(MapsDAO.KEY)
                        .append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "").append(MapsDAO.CODE).append(" TEXT, ")
                        .append(MapsDAO.PARENT_CODE).append(" TEXT, ").append(MapsDAO.REGION).append(" TEXT, ")
                        .append(MapsDAO.NAMES).append(" TEXT, " + "").append(MapsDAO.SKM_FILE_PATH).append(" TEXT, ")
                        .append(MapsDAO.ZIP_FILE_PATH).append(" TEXT, ").append(MapsDAO.TXG_FILE_PATH).append(" TEXT, ")
                        .append(MapsDAO.TXG_FILE_SIZE).append(" INTEGER, " + "").append(MapsDAO.SKM_AND_ZIP_FILES_SIZE)
                        .append(" INTEGER, ").append(MapsDAO.SKM_FILE_SIZE).append(" INTEGER, " + "")
                        .append(MapsDAO.UNZIPPED_FILE_SIZE).append(" INTEGER, ").append(MapsDAO.BOUNDING_BOX_LATITUDE_MAX)
                        .append(" DOUBLE, ").append(MapsDAO.BOUNDING_BOX_LATITUDE_MIN).append(" DOUBLE, ")
                        .append(MapsDAO.BOUNDING_BOX_LONGITUDE_MAX).append(" DOUBLE, ")
                        .append(MapsDAO.BOUNDING_BOX_LONGITUDE_MIN).append(" DOUBLE, " + "").append(MapsDAO.SUBTYPE)
                        .append(" TEXT, ").append(MapsDAO.STATE).append(" INTEGER, ").append(MapsDAO.NO_DOWNLOADED_BYTES)
                        .append(" INTEGER, ").append(MapsDAO.FLAG_ID).append(" INTEGER, ").append(MapsDAO.DOWNLOAD_PATH)
                        .append(" TEXT)").toString();
        return createMapResourcesTable;
    }

    private String createTollsTable() {
        String create = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append("Tolls").append(" (")
                .append("Id").append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ").append("Name")
                .append(" TEXT, ").append("RoadNr").append(" TEXT, ").append("Latitude").append(" TEXT, ")
                .append("Longitude").append(" TEXT, ").append("CountryCode").append(" TEXT, ")
                .append("Cost").append(" REAL)").toString();
        return create;
    }

    private String createVignetteHighwaysTable() {
        String create = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append("VignetteHighways").append(" (")
                .append("Id").append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ").append("RoadNr")
                .append(" TEXT, ").append("CountryCode").append(" TEXT)").toString();
        return create;
    }

    private String createAvgFuelCostsTable() {
        String create = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append("AvgFuelCosts").append(" (")
                .append("Id").append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ")
                .append("CountryCode").append(" TEXT, ").append("PetrolCost").append(" REAL, ")
                .append("DieselCost").append(" REAL, ").append("LPGCost").append(" REAL, ")
                .append("MotorwayPattern").append(" TEXT, ").append("TrunkPattern").append(" TEXT)").toString();
        return create;
    }

    private String dropTollsTableQuery() {
        String drop = new StringBuilder("DROP TABLE IF EXISTS ").append("Tolls;").toString();
        return drop;
    }

    private String dropVignetteTableQuery() {
        String drop = new StringBuilder("DROP TABLE IF EXISTS ").append("VignetteHighways;").toString();
        return drop;
    }

    private String dropFuelTableQuery() {
        String drop = new StringBuilder("DROP TABLE IF EXISTS ").append("AvgFuelCosts;").toString();
        return drop;
    }

    private String fillTollsTable(String[] values) {
        int id = Integer.parseInt(values[0].substring(1, values[0].length()-1));
        String name = values[1];
        String road_nr = values[2];
        String latitude = values[3];
        String longitude = values[4];
        String country_code = values[5];
        Double cost = Double.parseDouble(values[6].substring(1, values[6].length()-1));
        String fill = new StringBuilder("INSERT INTO ").append("Tolls").append(" VALUES(")
                .append(id).append(", ").append(name).append(", ").append(road_nr).append(", ")
                .append(latitude).append(", ").append(longitude).append(", ").append(country_code).append(", ")
                .append(cost).append(")").toString();
        return fill;
    }

    private String fillVignetteHighwaysTable(String[] values) {
        int id = Integer.parseInt(values[0].substring(1, values[0].length()-1));
        String road_nr = values[1];
        String country_code = values[2];
        String fill = new StringBuilder("INSERT INTO ").append("VignetteHighways").append(" VALUES(")
                .append(id).append(", ").append(road_nr).append(", ").append(country_code).append(")").toString();
        return fill;
    }

    private String fillAvgFuelCostsTable(String[] values) {
        int id = Integer.parseInt(values[0].substring(1, values[0].length()-1));
        String country_code = values[1];
        Double petrolCost = Double.parseDouble(values[2].substring(1, values[2].length()-1));
        Double dieselCost = Double.parseDouble(values[3].substring(1, values[3].length()-1));
        Double LPGCost = Double.parseDouble(values[4].substring(1, values[4].length()-1));
        String motorwayPattern = values[5];
        String trunkPattern = values[6];
        String fill = new StringBuilder("INSERT INTO ").append("AvgFuelCosts").append(" VALUES(")
                .append(id).append(", ").append(country_code).append(", ").append(petrolCost).append(", ")
                .append(dieselCost).append(", ").append(LPGCost).append(", ").append(motorwayPattern).append(", ")
                .append(trunkPattern).append(")").toString();
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
