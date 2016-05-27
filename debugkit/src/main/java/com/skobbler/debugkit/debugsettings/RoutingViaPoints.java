package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.search.SKSearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mirceab on 03.07.2015.
 */
public class RoutingViaPoints extends DebugSettings implements SKMapSurfaceListener {

    /**
     * Long tap select viapoint check
     */
    private boolean isViaPointSelected;

    /**
     * Viapoint list
     */
    private static ArrayList<SKViaPoint> viaPointList = new ArrayList<SKViaPoint>();

    /**
     * POIs to be detected on route
     */
    private Map<Integer, SKViaPoint> viaPointsOnMap;
    /**
     * The via point(long/lat) for the route
     */
    private SKViaPoint viaPoint;
    /**
     * Identifier
     */
    private int identifier = 2;
    /**
     * Remove identifier
     */
    private int removeIdentifier = 2;
    /**
     * Latitude
     */
    private double latitude = 37.7777;
    /**
     * longitude
     */
    private double longitude = -122.4200;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        viaPointsOnMap = new HashMap<Integer, SKViaPoint>();
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoint_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_identifier), identifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.latitude), latitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.longitude), longitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoint_longtap), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoint_add), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_identifier), removeIdentifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoint_remove), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_clear_all), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_via_point_option;
    }

    @Override
    void defineSpecificListeners() {
        final View addViaPoint = specificLayout.findViewById(R.id.viapoint_add_viapoint);
        final View removeViaPoint = specificLayout.findViewById(R.id.viapoint_remove_identifier_viapoint);
        final View longTapAddViaPoint = specificLayout.findViewById(R.id.viapoint_longpress_start);
        final View clearAll = specificLayout.findViewById(R.id.viapoint_remove_all);
        final EditText identifierPoint = (EditText) specificLayout.findViewById(R.id.viapoint_identifier).findViewById(R.id.property_value);
        final EditText startLatitude = (EditText) specificLayout.findViewById(R.id.viapoint_latitude).findViewById(R.id.property_value);
        final EditText startLongitude = (EditText) specificLayout.findViewById(R.id.viapoint_longitude).findViewById(R.id.property_value);
        final EditText removeIdentifierEditText = (EditText) specificLayout.findViewById(R.id.viapoint_remove_identifier).findViewById(R.id.property_value);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 2; i <= identifier; i++) {
                    SKRouteManager.getInstance().removeViaPoint(i);
                    activity.getMapView().deleteAnnotation(i);
                    viaPointList.clear();
                }
                identifier=2;
            }
        });

        addViaPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifier = Integer.parseInt(identifierPoint.getText().toString());
                latitude = Double.parseDouble(startLatitude.getText().toString());
                longitude = Double.parseDouble(startLongitude.getText().toString());
                launchViaPointAdd(new SKCoordinate(longitude, latitude));
                isViaPointSelected = false;
            }
        });
        removeViaPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeIdentifier = Integer.parseInt(removeIdentifierEditText.getText().toString());
                SKRouteManager.getInstance().removeViaPoint(removeIdentifier);
                activity.getMapView().deleteAnnotation(removeIdentifier);
            }
        });
        longTapAddViaPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isViaPointSelected = true;
            }
        });
    }

    public static ArrayList<SKViaPoint> getViaPointList() {
        return viaPointList;
    }

    @Override
    void onOpened() {
        super.onOpened();
        activity.getMapHolder().setMapSurfaceListener(this);

    }

    @Override
    void onClose() {
        super.onClose();
    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {

    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
        SKCoordinate poiCoordinates = activity.getMapView().pointToCoordinate(skScreenPoint);
        final SKSearchResult place = SKReverseGeocoderManager
                .getInstance().reverseGeocodePosition(poiCoordinates);
        if (place != null && isViaPointSelected) {
            launchViaPointAdd(place.getLocation());
        }
    }

    private void launchViaPointAdd(SKCoordinate skCoordinate) {
        SKAnnotation annotation = new SKAnnotation(SKAnnotation.SK_ANNOTATION_TYPE_MARKER);
        annotation.setUniqueID(identifier);
        annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_MARKER);
        viaPoint = new SKViaPoint(identifier, skCoordinate);
        viaPoint.setUniqueId(identifier);
        viaPointList.add(viaPoint);
        viaPointsOnMap.put(identifier, viaPoint);
        identifier++;
        annotation.setLocation(skCoordinate);
        annotation.setMininumZoomLevel(5);
        activity.getMapView().addAnnotation(annotation,
                SKAnimationSettings.ANIMATION_NONE);
        // center map on a position
        activity.getMapView().setZoom(15);
        activity.getMapView().centerMapOnPosition(skCoordinate);
    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation) {

    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }
}
