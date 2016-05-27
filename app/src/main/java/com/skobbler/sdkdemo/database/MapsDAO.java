package com.skobbler.sdkdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import com.skobbler.ngx.sdktools.download.SKToolsDownloadItem;
import com.skobbler.ngx.util.SKLogging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods for accessing the "Maps" table
 * Created by CatalinM on 11/10/2014.
 */
public class MapsDAO {

    /**
     * US country code
     */
    public static final String US_CODE = "US";

    /**
     * ENGLISH language code
     */
    public static final String ENGLISH_LANGUAGE_CODE = "en";

    /**
     * name of the maps data table
     */
    public static final String MAPS_TABLE = "Maps";

    /**
     * map code column key
     */
    public static final String CODE = "Code";

    /**
     * map parent code column key
     */
    public static final String PARENT_CODE = "ParentCode";

    /**
     * region column key (has a value only for state column)
     */
    public static final String REGION = "Region";

    /**
     * map names column key
     */
    public static final String NAMES = "Names";

    /**
     * map SKM file path column key
     */
    public static final String SKM_FILE_PATH = "SkmFilePath";

    /**
     * map ZIP file path column key
     */
    public static final String ZIP_FILE_PATH = "ZipFilePath";

    /**
     * map TXG file path column key
     */
    public static final String TXG_FILE_PATH = "TxgFilePath";

    /**
     * map TXG file size column key
     */
    public static final String TXG_FILE_SIZE = "TxgFileSize";

    /**
     * map total size(SKM + ZIP) files size column key
     */
    public static final String SKM_AND_ZIP_FILES_SIZE = "SkmAndZipFilesSize";

    /**
     * map SKM file size column key
     */
    public static final String SKM_FILE_SIZE = "SkmFileSize";

    /**
     * map UNZIPPED file size column key
     */
    public static final String UNZIPPED_FILE_SIZE = "UnzippedFileSize";

    /**
     * map sub-type column key
     */
    public static final String SUBTYPE = "SubType";

    /**
     * map state column key
     */
    public static final String STATE = "State";

    /**
     * Bounding box column keys
     */
    public static final String BOUNDING_BOX_LONGITUDE_MIN = "LongMin";

    public static final String BOUNDING_BOX_LONGITUDE_MAX = "LongMax";

    public static final String BOUNDING_BOX_LATITUDE_MIN = "LatMin";

    public static final String BOUNDING_BOX_LATITUDE_MAX = "LatMax";

    /**
     * map no bytes column key
     */
    public static final String NO_DOWNLOADED_BYTES = "NoDownloadedBytes";

    /**
     * flag ID
     */
    public static final String FLAG_ID = "FlagID";

    /**
     * download path
     */
    public static final String DOWNLOAD_PATH = "DownloadPath";

    /**
     * Prefix for the flag image resources
     */
    public static final String FLAG_BIG_ICON_PREFIX = "icon_flag_big_";

    /**
     * map type column values
     */
    public static final String CONTINENT_TYPE = "continent";

    public static final String COUNTRY_TYPE = "country";

    public static final String REGION_TYPE = "region";

    public static final String CITY_TYPE = "city";

    public static final String STATE_TYPE = "state";

    /**
     * tag for the class
     */
    private static final String TAG = "MapsDAO";

    /**
     * auto-increment column key (primary key ID)
     */
    public static final String KEY = "Key";

    /**
     * the associated resources DAO
     */
    private final ResourcesDAO resourcesDAO;

    /**
     * constructs an object of this type
     *
     * @param resourcesDAO resourcesDAO
     */
    public MapsDAO(ResourcesDAO resourcesDAO) {
        this.resourcesDAO = resourcesDAO;
    }

    /**
     * insert the maps and codes into resources database (maps table)
     *
     * @param maps               map objects that will be inserted into database
     * @param mapsItemsCodes     a map representing the maps hierarchy defined in JSON file
     * @param regionItemsCodes   a map representing the regions hierarchy defined in JSON file
     * @param applicationContext application context
     */
    public void insertMaps(List<MapDownloadResource> maps, Map<String, String> mapsItemsCodes, Map<String, String> regionItemsCodes, Context applicationContext) {
        try {
            if ((maps != null) && (mapsItemsCodes != null) && (regionItemsCodes != null)) {
                // create a compile statement for inserting the maps using transactions
                StringBuilder insertCommand = new StringBuilder("INSERT INTO ");
                insertCommand.append(MAPS_TABLE).append(" VALUES (?");
                // the number of columns in maps table is 20
                for (int i = 0; i < 20; i++) {
                    insertCommand.append(",?");
                }
                insertCommand.append(");");
                resourcesDAO.getDatabase().beginTransaction();
                SQLiteStatement insertStatement = resourcesDAO.getDatabase().compileStatement(insertCommand.toString());
                int columnIndex, lineIndex = 0;
                for (MapDownloadResource map : maps) {
                    columnIndex = 1;
                    lineIndex++;
                    insertStatement.clearBindings();
                    insertStatement.bindLong(columnIndex++, lineIndex);
                    insertStatement.bindString(columnIndex++, map.getCode());
                    insertStatement.bindString(columnIndex++, mapsItemsCodes.get(map.getCode()));
                    if ((map.getSubType() != null) && map.getSubType().equalsIgnoreCase(STATE_TYPE)) {
                        insertStatement.bindString(columnIndex++, regionItemsCodes.get(map.getCode()));
                    } else {
                        insertStatement.bindString(columnIndex++, "");
                    }

                    // compute the string that contains all the name translations
                    StringBuilder nameInAllSpecifiedLanguages = new StringBuilder();

                    if (map.getNames() != null) {
                        for (final java.util.Map.Entry<String, String> currentEntry : map.getNames().entrySet()) {
                            nameInAllSpecifiedLanguages.append(currentEntry.getKey()).append("=")
                                    .append(currentEntry.getValue()).append(";");
                        }
                    }

                    if (nameInAllSpecifiedLanguages.length() > 1) {
                        insertStatement.bindString(columnIndex++,
                                nameInAllSpecifiedLanguages.substring(0, nameInAllSpecifiedLanguages.length() - 1));
                    } else {
                        insertStatement.bindString(columnIndex++, "");
                    }
                    insertStatement.bindString(columnIndex++, map.getSKMFilePath());
                    insertStatement.bindString(columnIndex++, map.getZipFilePath());
                    insertStatement.bindString(columnIndex++, map.getTXGFilePath());
                    insertStatement.bindLong(columnIndex++, (int) map.getTXGFileSize());
                    insertStatement.bindLong(columnIndex++, (int) map.getSkmAndZipFilesSize());
                    insertStatement.bindLong(columnIndex++, (int) map.getSkmFileSize());
                    insertStatement.bindLong(columnIndex++, (int) map.getUnzippedFileSize());
                    insertStatement.bindDouble(columnIndex++, map.getBbLatMax());
                    insertStatement.bindDouble(columnIndex++, map.getBbLatMin());
                    insertStatement.bindDouble(columnIndex++, map.getBbLongMax());
                    insertStatement.bindDouble(columnIndex++, map.getBbLongMin());
                    insertStatement.bindString(columnIndex++, map.getSubType());
                    insertStatement.bindLong(columnIndex++, map.getDownloadState());
                    insertStatement.bindLong(columnIndex++, map.getNoDownloadedBytes());
                    insertStatement.bindLong(columnIndex++, 0);
                    insertStatement.bindString(columnIndex, map.getDownloadPath());
                    insertStatement.execute();
                }
            }
        } finally {
            if ((maps != null) && (mapsItemsCodes != null)) {
                SKLogging.writeLog(TAG, "Maps were inserted into database !!!", SKLogging.LOG_DEBUG);
                // close the GENERAL transaction
                resourcesDAO.getDatabase().setTransactionSuccessful();
                resourcesDAO.getDatabase().endTransaction();
            }
        }
    }

    public void deleteMaps() {
        String deleteCommand = "DELETE FROM " + MAPS_TABLE;
        SQLiteStatement deleteStatement = resourcesDAO.getDatabase().compileStatement(deleteCommand.toString());
        deleteStatement.execute();
    }

    /**
     * get all maps from DB (countries, cities or us states)
     *
     * @return all maps of a certain type from database
     */
    public Map<String, MapDownloadResource> getAvailableMapsForACertainType(String... mapType) {
        final StringBuilder query =
                new StringBuilder("SELECT ").append(CODE).append(", ").append(PARENT_CODE).append(", ").append(REGION).append(", ")
                        .append(NAMES).append(", ").append(SKM_FILE_PATH).append(", " +
                        "").append(ZIP_FILE_PATH).append(", ")
                        .append(TXG_FILE_PATH).append(", ").append(TXG_FILE_SIZE).append(", ")
                        .append(SKM_AND_ZIP_FILES_SIZE).append(", ").append(SKM_FILE_SIZE).append(", " +
                        "").append(UNZIPPED_FILE_SIZE)
                        .append(", ").append(BOUNDING_BOX_LATITUDE_MAX).append(", ").append(BOUNDING_BOX_LATITUDE_MIN)
                        .append(", ").append(BOUNDING_BOX_LONGITUDE_MAX).append(", ").append(BOUNDING_BOX_LONGITUDE_MIN)
                        .append(", ").append(SUBTYPE).append(", ").append(STATE).append(", " +
                        "").append(NO_DOWNLOADED_BYTES)
                        .append(", ").append(FLAG_ID).append(", ").append(DOWNLOAD_PATH).append(" FROM ").append
                        (MAPS_TABLE);
        if ((mapType != null) && (mapType.length > 0)) {
            query.append(" WHERE ").append(SUBTYPE).append("=?");
            for (int i = 1; i < mapType.length; i++) {
                query.append(" or ").append(SUBTYPE).append("=?");
            }
        }
        Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query.toString(), mapType);
        if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
            Map<String, MapDownloadResource> maps = new HashMap<String, MapDownloadResource>();
            MapDownloadResource currentMap;
            try {
                resultCursor.moveToFirst();
                while (!resultCursor.isAfterLast()) {
                    currentMap = new MapDownloadResource();
                    currentMap.setCode(resultCursor.getString(0));
                    currentMap.setParentCode(resultCursor.getString(1));
                    currentMap.setNames(resultCursor.getString(3));
                    currentMap.setSkmFilePath(resultCursor.getString(4));
                    currentMap.setZipFilePath(resultCursor.getString(5));
                    currentMap.setTXGFilePath(resultCursor.getString(6));
                    currentMap.setTXGFileSize(resultCursor.getInt(7));
                    currentMap.setSkmAndZipFilesSize(resultCursor.getInt(8));
                    currentMap.setSkmFileSize(resultCursor.getInt(9));
                    currentMap.setUnzippedFileSize(resultCursor.getInt(10));
                    currentMap.setBbLatMax(resultCursor.getDouble(11));
                    currentMap.setBbLatMin(resultCursor.getDouble(12));
                    currentMap.setBbLongMax(resultCursor.getDouble(13));
                    currentMap.setBbLongMin(resultCursor.getDouble(14));
                    currentMap.setSubType(resultCursor.getString(15));
                    currentMap.setDownloadState((byte) resultCursor.getInt(16));
                    currentMap.setNoDownloadedBytes(resultCursor.getInt(17));
                    currentMap.setFlagID(resultCursor.getInt(18));
                    currentMap.setDownloadPath(resultCursor.getString(19));
                    maps.put(currentMap.getCode(), currentMap);
                    resultCursor.moveToNext();
                }
            } finally {
                resultCursor.close();
            }

            return maps;
        } else {
            if (resultCursor != null) {
                resultCursor.close();
            }
            return null;
        }
    }

    /**
     * Updates the database record corresponding to the map resource given as parameter
     *
     * @param mapResource
     */
    public void updateMapResource(MapDownloadResource mapResource) {
        final ContentValues values = new ContentValues();
        values.put(STATE, mapResource.getDownloadState());
        values.put(NO_DOWNLOADED_BYTES, mapResource.getNoDownloadedBytes());
        values.put(SKM_FILE_PATH, mapResource.getSKMFilePath());
        values.put(SKM_FILE_SIZE, mapResource.getSkmFileSize());
        values.put(TXG_FILE_PATH, mapResource.getTXGFilePath());
        values.put(TXG_FILE_SIZE, mapResource.getTXGFileSize());
        values.put(ZIP_FILE_PATH, mapResource.getZipFilePath());
        values.put(SKM_AND_ZIP_FILES_SIZE, mapResource.getSkmAndZipFilesSize());
        values.put(UNZIPPED_FILE_SIZE, mapResource.getUnzippedFileSize());
        values.put(DOWNLOAD_PATH, mapResource.getDownloadPath());
        try {
            resourcesDAO.getDatabase().beginTransaction();
            resourcesDAO.getDatabase().update(MAPS_TABLE, values, CODE + "=?", new String[]{mapResource.getCode()});
            resourcesDAO.getDatabase().setTransactionSuccessful();
        } catch (final SQLException e) {
            SKLogging.writeLog(TAG, "SQL EXCEPTION SAVE MAP DATA " + e.getMessage(), SKLogging.LOG_ERROR);
        } finally {
            resourcesDAO.getDatabase().endTransaction();
        }
    }

    /**
     * Marks resources that are presently in the download queue as not queued in the database table
     */
    public void clearResourcesInDownloadQueue() {
        final ContentValues values = new ContentValues();
        values.put(STATE, SKToolsDownloadItem.NOT_QUEUED);
        values.put(NO_DOWNLOADED_BYTES, 0);
        try {
            resourcesDAO.getDatabase().beginTransaction();
            resourcesDAO.getDatabase().update(MAPS_TABLE, values, STATE + "=? OR " + STATE + "=? OR " + STATE + "=?",
                    new String[]{Byte.toString(SKToolsDownloadItem.DOWNLOADING), Byte.toString(SKToolsDownloadItem.PAUSED),
                            Byte.toString(SKToolsDownloadItem.QUEUED)});
            resourcesDAO.getDatabase().setTransactionSuccessful();
        } catch (final SQLException e) {
            SKLogging.writeLog(TAG, "SQL EXCEPTION SAVE MAP DATA " + e.getMessage(), SKLogging.LOG_ERROR);
        } finally {
            resourcesDAO.getDatabase().endTransaction();
        }
    }
}