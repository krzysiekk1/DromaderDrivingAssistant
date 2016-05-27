package com.skobbler.sdkdemo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.sdkdemo.R;

/**
 * Created by Filip Tudic on 27-Mar-15.
 */
public class MapCacheActivity extends Activity implements SKMapSurfaceListener, SKNavigationListener, SKRouteListener {

    private static final String TAG = "MapCacheActivity";

    private SKMapSurfaceView mapView;

    private SKMapViewHolder mapViewGroup;

    private SKCoordinate currentPosition = new SKCoordinate(23.593823f, 46.773716f);

    private SKCoordinate routeDestinationPoint = new SKCoordinate(23.596824f, 46.770088f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cache);
        mapViewGroup = (SKMapViewHolder) findViewById(R.id.view_group_map);
        mapViewGroup.setMapSurfaceListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewGroup.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewGroup.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewGroup = null;
    }

    @Override
    public void onActionPan() {
    }

    @Override
    public void onActionZoom() {
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
        mapView = skMapViewHolder.getMapSurfaceView();
        final RelativeLayout chessBackground = (RelativeLayout) findViewById(R.id.chess_board_background);
        chessBackground.setVisibility(View.GONE);
        mapView = mapViewGroup.getMapSurfaceView();
        mapView.setPositionAsCurrent(currentPosition, 0, true);
        mapView.setZoom(17);
        SKNavigationManager.getInstance().setNavigationListener(this);
        addStartDestinationPins();
        launchRouteCalculation(currentPosition, routeDestinationPoint);
    }

    /**
     * Add the route pins (green pin for route start point and red pin for the destination point)
     */
    private void addStartDestinationPins() {
        // get the annotation object
        SKAnnotation annotation1 = new SKAnnotation(10);
        // set annotation location
        annotation1.setLocation(routeDestinationPoint);
        // set minimum zoom level at which the annotation should be visible
        annotation1.setMininumZoomLevel(5);
        // set the annotation's type
        annotation1.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
        // render annotation on map
        mapView.addAnnotation(annotation1, SKAnimationSettings.ANIMATION_NONE);

        SKAnnotation annotation2 = new SKAnnotation(11);
        annotation2.setLocation(currentPosition);
        annotation2.setMininumZoomLevel(5);
        annotation2.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        mapView.addAnnotation(annotation2, SKAnimationSettings.ANIMATION_NONE);
    }

    /**
     * Launches a single route calculation
     */
    private void launchRouteCalculation(SKCoordinate startPoint, SKCoordinate destinationPoint) {
        // get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();
        // set start and destination points
        route.setStartCoordinate(startPoint);
        route.setDestinationCoordinate(destinationPoint);
        // set the number of routes to be calculated
        route.setNoOfRoutes(1);
        // set the route mode
        route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);
        // set whether the route should be shown on the map after it's computed
        route.setRouteExposed(true);
        // set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(this);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion region) {
    }

    @Override
    public void onDoubleTap(SKScreenPoint point) {
    }

    @Override
    public void onSingleTap(SKScreenPoint point) {
    }

    @Override
    public void onRotateMap() {
    }

    @Override
    public void onLongPress(SKScreenPoint point) {
    }

    @Override
    public void onInternetConnectionNeeded() {
    }

    @Override
    public void onMapActionDown(SKScreenPoint point) {
    }

    @Override
    public void onMapActionUp(SKScreenPoint point) {
    }

    @Override
    public void onMapPOISelected(SKMapPOI mapPOI) {
    }

    @Override
    public void onAnnotationSelected(SKAnnotation annotation) {
    }

    @Override
    public void onCompassSelected() {
    }

    @Override
    public void onInternationalisationCalled(int result) {
    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI customPoi) {
    }

    @Override
    public void onDestinationReached() {
    }

    @Override
    public void onUpdateNavigationState(SKNavigationState navigationState) {

    }

    @Override
    public void onReRoutingStarted() {
    }

    @Override
    public void onFreeDriveUpdated(String countryCode, String streetName, String referenceName, SKNavigationState.SKStreetType streetType,
                                   double currentSpeed, double speedLimit) {
    }

    @Override
    public void onViaPointReached(int index) {
    }

    @Override
    public void onVisualAdviceChanged(boolean firstVisualAdviceChanged, boolean secondVisualAdviceChanged,
                                      SKNavigationState navigationState) {
    }

    @Override
    public void onPOIClusterSelected(SKPOICluster arg0) {
    }

    @Override
    public void onTunnelEvent(boolean arg0) {
    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion arg0) {
    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion arg0) {
    }

    @Override
    public void onCurrentPositionSelected() {
    }

    @Override
    public void onObjectSelected(int arg0) {
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSignalNewAdviceWithInstruction(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSpeedExceededWithInstruction(String arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBoundingBoxImageRendered(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGLInitializationError(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {

    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {

    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }
}
