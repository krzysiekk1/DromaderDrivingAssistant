package com.skobbler.debugkit.activity;

import android.widget.Toast;

import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.positioner.SKPosition;

/**
 * Singleton class that provides various methods for changing the state of the
 * map.
 */
public class DebugMapOperationsManager {
    /**
     * Singleton instance of this class
     */
    private static DebugMapOperationsManager instance;
    /**
     * the map surface view
     */
    private SKMapSurfaceView mapView;
    /**
     * Gets the {@link DebugMapOperationsManager} object
     * @return
     */
    public static DebugMapOperationsManager getInstance() {
        if (instance == null) {
            instance = new DebugMapOperationsManager();
        }
        return instance;
    }
    /**
     * Sets the map view, necessary for handling operations on it.
     * @param mapView
     */
    public void setMapView(SKMapSurfaceView mapView) {
        this.mapView = mapView;
    }
    /**
     * Sets map in panning mode.
     */
    public void startPanningMode() {

        SKMapSettings mapSettings = mapView.getMapSettings();
        mapSettings.setInertiaPanningEnabled(true);
        mapSettings.setMapZoomingEnabled(true);
        mapSettings.setMapPanningEnabled(false);
        mapSettings.setMapRotationEnabled(true);
        mapView.getMapSettings().setCompassPosition(new SKScreenPoint(5, 5));
        mapView.getMapSettings().setCompassShown(true);
        mapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.NONE_WITH_HEADING);
        mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_2D);
    }

    /**
     * Center map on current position
     * @param currentPosition
     */
    public void centerMapOnCurrentPosition(SKPosition currentPosition){
        if (mapView != null && currentPosition != null) {
            mapView.centerMapOnCurrentPositionSmooth(17, 500);
        }
    }
}
