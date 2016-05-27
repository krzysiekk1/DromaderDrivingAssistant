package com.skobbler.sdkdemo.database;

import android.content.Context;

/**
 * This class provides methods for accessing the database tables
 * Created by CatalinM on 11/11/2014.
 */
public class ResourcesDAOHandler {

    /**
     * Singleton instance for current class
     */
    private static ResourcesDAOHandler instance;

    /**
     * the database object for maps table
     */
    private MapsDAO mapsDAO;

    /**
     * constructs a ResourcesDAOHandler object
     * @param context application context
     */
    private ResourcesDAOHandler(Context context) {
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(context);
        resourcesDAO.openDatabase();
        mapsDAO = new MapsDAO(resourcesDAO);
    }

    /**
     * gets an instance of ResourcesDAOHandler object
     * @param context application context
     * @return an instance of ResourcesDAOHandler object
     */
    public static ResourcesDAOHandler getInstance(Context context) {
        if (instance == null) {
            instance = new ResourcesDAOHandler(context);
        }
        return instance;
    }

    /**
     * gets the maps DAO object
     * @return maps DAO object
     */
    public MapsDAO getMapsDAO() {
        return mapsDAO;
    }
}