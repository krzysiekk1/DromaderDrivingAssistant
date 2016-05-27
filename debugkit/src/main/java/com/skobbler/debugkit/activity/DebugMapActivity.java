package com.skobbler.debugkit.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.adapter.MenuDrawerAdapter;
import com.skobbler.debugkit.debugsettings.AnimateDebugSettings;
import com.skobbler.debugkit.debugsettings.AnnotationCustomPoiDebugSettings;
import com.skobbler.debugkit.debugsettings.BoundingBoxDebugSettings;
import com.skobbler.debugkit.debugsettings.CalloutViewDebugSettings;
import com.skobbler.debugkit.debugsettings.CircleDebugSettings;
import com.skobbler.debugkit.debugsettings.CurrentPositionDebugSettings;
import com.skobbler.debugkit.debugsettings.DebugSettings;
import com.skobbler.debugkit.debugsettings.InternationalizationDebugSettings;
import com.skobbler.debugkit.debugsettings.MapCacheDebugSettings;
import com.skobbler.debugkit.debugsettings.MapDebugSettings;
import com.skobbler.debugkit.debugsettings.MapStateDebugSettings;
import com.skobbler.debugkit.debugsettings.MapStyleDebugSettings;
import com.skobbler.debugkit.debugsettings.RoutingDebugSettings;
import com.skobbler.debugkit.debugsettings.PoiTrackerDebugSettings;
import com.skobbler.debugkit.debugsettings.PositionLoggingDebugSettings;
import com.skobbler.debugkit.debugsettings.ScaleViewDebugSettings;
import com.skobbler.debugkit.debugsettings.OverlaysDebugSettings;
import com.skobbler.debugkit.debugsettings.PolygonDebugSettings;
import com.skobbler.debugkit.debugsettings.PolylineDebugSettings;
import com.skobbler.debugkit.debugsettings.ScreenshotDebugSettings;
import com.skobbler.debugkit.debugsettings.VersionInfoDebugSettings;
import com.skobbler.debugkit.model.MenuDrawerItem;
import com.skobbler.debugkit.util.DebugKitUtils;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKBoundingBox;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.map.realreach.SKRealReachListener;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.poitracker.SKDetectedPOI;
import com.skobbler.ngx.poitracker.SKPOITrackerListener;
import com.skobbler.ngx.poitracker.SKTrackablePOIType;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationListener;
import com.skobbler.ngx.versioning.SKMapUpdateListener;
import com.skobbler.ngx.versioning.SKVersioningManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Filip Tudic on 20-May-15.
 */
public class DebugMapActivity extends Activity implements SKMapSurfaceListener, SKRouteListener, SKNavigationListener,
        SKRealReachListener, SKPOITrackerListener, SKCurrentPositionListener, SensorEventListener,
        SKMapUpdateListener, SKToolsNavigationListener {

    /**
     * time, in milliseconds, from the moment when the application receives new
     * GPS values
     */
    private static final int MINIMUM_TIME_UNTILL_MAP_CAN_BE_UPDATED = 30;

    /**
     * defines how smooth the movement will be (1 is no smoothing and 0 is never
     * updating).
     */
    private static final float SMOOTH_FACTOR_COMPASS = 0.1f;

    private static int runningInstances = 0;

    /**
     * the current value of the z axis ; at each new step it is updated with the
     * new value
     */
    private float currentCompassValue;

    /**
     * last time when received GPS signal
     */
    private long lastTimeWhenReceivedGpsSignal;

    /**
     * Current position provider
     */
    private SKCurrentPositionProvider currentPositionProvider;

    /**
     * Current position
     */
    private SKPosition currentPosition;

    /**
     * the view that holds the map view
     */
    private SKMapViewHolder mapViewGroup;

    /**
     * Surface view for displaying the map
     */
    private SKMapSurfaceView mapView;
    /**
     * menu items
     */
    private LinkedHashMap<TestingOption, MenuDrawerItem> menuItems;
    /**
     * menu items values
     */
    private ArrayList<MenuDrawerItem> list;

    /**
     * Drawer layout object
     */
    private DrawerLayout drawerLayout;

    /**
     * The navigation drawer
     */
    private ListView drawerList;

    /**
     * Action bar toggle
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private LinearLayout debugBaseLayout;

    /**
     * the values returned by magnetic sensor
     */
    private float[] orientationValues;

    /**
     * the latest exact screen orientation (given by the
     * getExactScreenOrientation method) that was recorded
     */
    private int lastExactScreenOrientation = -1;

    private SKAnnotation testAnnotation;

    public enum TestingOption {
        TESTING_OPTION, ANNOTATION_OPTION, MAP_VIEW_SETTINGS_OPTION, MAP_CACHE_OPTION, LAST_RENDERED_FRAME_OPTION, ANIMATION_CUSTOM_VIEW_OPTION, BOUNDING_BOX_OPTION, INTERNALIZATION_OPTION, ANIMATE_OPTION, MAP_STYLE_OPTION, SCALE_VIEW_OPTION, CALLOUT_VIEW_OPTION,
        ROUTING_OPTION, MAP_VERSION_OPTION, OVERLAYS_OPTION, POSITION_LOGGING_OPTION, POI_TRACKER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runningInstances++;

        setContentView(R.layout.debug_activity_map);
        //app = (DemoApplication) getApplication();

        currentPositionProvider = new SKCurrentPositionProvider(this);
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(true, true, false);

        mapViewGroup = (SKMapViewHolder) findViewById(R.id.view_group_map);
        mapViewGroup.setMapSurfaceListener(DebugMapActivity.this);
//        LayoutInflater inflater = (LayoutInflater) getService(Context.LAYOUT_INFLATER_SERVICE);
//        mapPopup = mapViewGroup.getCalloutView();
//        View view = inflater.inflate(R.layout.layout_popup, null);
//        popupTitleView = (TextView) view.findViewById(R.id.top_text);
//        popupDescriptionView = (TextView) view.findViewById(R.id.bottom_text);
//        mapPopup.setCustomView(view);

        SKVersioningManager.getInstance().setMapUpdateListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_launcher,
                R.string.open_drawer,
                R.string.close_drawer);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        debugBaseLayout = (LinearLayout) findViewById(R.id.debug_settings_base);

        initializeMenuItems();
    }


    /**
     * Initializes the navigation drawer list items
     */
    public void initializeMenuItems() {
        menuItems = new LinkedHashMap<TestingOption, MenuDrawerItem>();
        menuItems.put(TestingOption.TESTING_OPTION, create(TestingOption.TESTING_OPTION, getResources().getString(R.string.testing_section).toUpperCase(), MenuDrawerItem.SECTION_TYPE));
        menuItems.put(TestingOption.ANNOTATION_OPTION, create(TestingOption.ANNOTATION_OPTION, getResources().getString(R.string.option_annotation_POI), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.MAP_VIEW_SETTINGS_OPTION, create(TestingOption.MAP_VIEW_SETTINGS_OPTION, getResources().getString(R.string.option_map_view_settings), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.MAP_CACHE_OPTION, create(TestingOption.MAP_CACHE_OPTION, getResources().getString(R.string.option_map_cache), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.LAST_RENDERED_FRAME_OPTION, create(TestingOption.LAST_RENDERED_FRAME_OPTION, getResources().getString(R.string.option_last_rendered_frame), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.ANIMATION_CUSTOM_VIEW_OPTION, create(TestingOption.ANIMATION_CUSTOM_VIEW_OPTION, getResources().getString(R.string.option_ccp_animation_custom_view), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.BOUNDING_BOX_OPTION, create(TestingOption.BOUNDING_BOX_OPTION, getResources().getString(R.string.option_bounding_box), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.INTERNALIZATION_OPTION, create(TestingOption.INTERNALIZATION_OPTION, getResources().getString(R.string.option_internalization), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.ANIMATE_OPTION, create(TestingOption.ANIMATE_OPTION, getResources().getString(R.string.option_animate), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.MAP_STYLE_OPTION, create(TestingOption.MAP_STYLE_OPTION, getResources().getString(R.string.option_map_style), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.SCALE_VIEW_OPTION, create(TestingOption.SCALE_VIEW_OPTION, getResources().getString(R.string.option_scale_view), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.CALLOUT_VIEW_OPTION, create(TestingOption.CALLOUT_VIEW_OPTION, getResources().getString(R.string.option_callout_view), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.ROUTING_OPTION, create(TestingOption.ROUTING_OPTION, getResources().getString(R.string.option_routing), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.MAP_VERSION_OPTION, create(TestingOption.MAP_VERSION_OPTION, getResources().getString(R.string.option_map_version_information), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.OVERLAYS_OPTION, create(TestingOption.OVERLAYS_OPTION, getResources().getString(R.string.option_overlays), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.POI_TRACKER, create(TestingOption.POI_TRACKER, getResources().getString(R.string.option_poi_tracker), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(TestingOption.POSITION_LOGGING_OPTION, create(TestingOption.POSITION_LOGGING_OPTION, getResources().getString(R.string.option_position_logging), MenuDrawerItem.ITEM_TYPE));

        list = new ArrayList<MenuDrawerItem>(menuItems.values());
        drawerList.setAdapter(new MenuDrawerAdapter(DebugMapActivity.this, R.layout.element_menu_drawer_item, list));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    /**
     * Creates menu drawer item(section item/list item)
     *
     * @param testingOption
     * @param label
     * @param itemType
     * @return
     */
    public static MenuDrawerItem create(TestingOption testingOption, String label, int itemType) {
        MenuDrawerItem menuDrawerItem = new MenuDrawerItem(testingOption);
        menuDrawerItem.setLabel(label);
        menuDrawerItem.setItemType(itemType);
        return menuDrawerItem;

    }

    public void addTestAnnotationAtPosition(SKCoordinate position) {
        if (testAnnotation == null) {
            testAnnotation = new SKAnnotation(155000);
            testAnnotation.setLocation(position);
            testAnnotation.setMininumZoomLevel(3);
            testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            mapView.addAnnotation(testAnnotation, SKAnimationSettings.ANIMATION_NONE);
        } else {
            testAnnotation.setLocation(position);
            mapView.updateAnnotation(testAnnotation);
        }
    }

    private void updateTestAnnotation() {
        if (testAnnotation != null) {
            if (testAnnotation.getAnnotationType() == SKAnnotation.SK_ANNOTATION_TYPE_RED) {
                testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            } else {
                testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
            }
            mapView.updateAnnotation(testAnnotation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapViewGroup.onResume();
        DebugSettings.currentMapActivity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewGroup.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCurrentPositionUpdate(SKPosition position) {
        this.currentPosition = position;
        SKPositionerManager.getInstance().reportNewGPSPosition(this.currentPosition);
    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder mapHolder) {
        View chessBackground = findViewById(R.id.chess_board_background);
        chessBackground.setVisibility(View.GONE);

        mapView = mapHolder.getMapSurfaceView();
//        applySettingsOnMapView();
        startOrientationSensor();
//        if(SplashActivity.newMapVersionDetected != 0){
//            showUpdateDialog(SplashActivity.newMapVersionDetected);
//        }

    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {
        if (DebugSettings.currentSettings != null && DebugSettings.currentSettings instanceof MapStateDebugSettings) {
            ((MapStateDebugSettings) DebugSettings.currentSettings).update();
        }

        if (DebugSettings.currentSettings != null && DebugSettings.currentSettings instanceof ScreenshotDebugSettings &&
                ((ScreenshotDebugSettings) DebugSettings.currentSettings).isContinuousScreenshotOn()) {
                    mapView.requestScreenshot();
        }
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
    public void onSingleTap(SKScreenPoint screenPoint) {
        if (DebugSettings.currentSettings instanceof CircleDebugSettings) {
            CircleDebugSettings circleSettings = (CircleDebugSettings) DebugSettings.currentSettings;
            if (circleSettings.drawOnTap) {
                circleSettings.drawCircleOnTap(screenPoint);
            }
        } else if (DebugSettings.currentSettings instanceof PolylineDebugSettings) {
            PolylineDebugSettings polylineSettings = (PolylineDebugSettings) DebugSettings.currentSettings;
            if (polylineSettings.drawOnTap) {
                polylineSettings.addPolylineNode(screenPoint);
            }
        } else if (DebugSettings.currentSettings instanceof PolygonDebugSettings) {
            PolygonDebugSettings polygonSettings = (PolygonDebugSettings) DebugSettings.currentSettings;
            if (polygonSettings.drawOnTap) {
                polygonSettings.addPolygonNode(screenPoint);
            }
        }

    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
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
        if (skAnnotation == testAnnotation) {
            updateTestAnnotation();
        }
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
    public void onObjectSelected(int id) {
        String overlayType = ((OverlaysDebugSettings) DebugSettings.getInstanceForType(OverlaysDebugSettings.class)).getOverlayTypeForId(id);
        Toast.makeText(this, "Tapped on " + overlayType + " #" + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {
        if (DebugSettings.currentSettings instanceof BoundingBoxDebugSettings) {
            BoundingBoxDebugSettings boundingBoxSettings = (BoundingBoxDebugSettings) DebugSettings.currentSettings;
            Toast.makeText(this,"Picture saved: " + boundingBoxSettings.getFilePath(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView screenshotView = (ImageView) findViewById(R.id.screenshot_view);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, screenshotView.getWidth(), screenshotView.getHeight(), false);
                screenshotView.setImageDrawable(new BitmapDrawable(getResources(), resizedBitmap));
            }
        });
    }

    @Override
    public void onDestinationReached() {

    }

    @Override
    public void onSignalNewAdviceWithInstruction(String s) {

    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceededWithInstruction(String s, boolean b) {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {

    }

    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String countryCode, String streetName, String referenceName, SKNavigationState.SKStreetType streetType,
                                   double currentSpeed, double speedLimit) {

    }

    @Override
    public void onViaPointReached(int i) {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {

    }

    @Override
    public void onTunnelEvent(boolean b) {

    }

    @Override
    public void onRealReachCalculationCompleted(SKBoundingBox skBoundingBox) {

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

    @Override
    public void onNavigationEnded() {

    }

    @Override
    public void onRouteCalculationStarted() {

    }

    @Override
    public void onRouteCalculationCompleted() {

    }

    @Override
    public void onRouteCalculationCanceled() {

    }

    @Override
    public void onNavigationStarted() {

    }

    @Override
    public void onNewVersionDetected(int i) {

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

    @Override
    public void onUpdatePOIsInRadius(double v, double v1, int i) {

    }

    @Override
    public void onReceivedPOIs(SKTrackablePOIType skTrackablePOIType, List<SKDetectedPOI> list) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle  other action bar items...
        return super.onOptionsItemSelected(item);
    }

    /**
     * Customize the map view
     */
    private void applySettingsOnMapView() {
        mapView.getMapSettings().setMapRotationEnabled(true);
        mapView.getMapSettings().setMapZoomingEnabled(true);
        mapView.getMapSettings().setMapPanningEnabled(true);
        mapView.getMapSettings().setZoomWithAnchorEnabled(true);
        mapView.getMapSettings().setInertiaRotatingEnabled(true);
        mapView.getMapSettings().setInertiaZoomingEnabled(true);
        mapView.getMapSettings().setInertiaPanningEnabled(true);
    }

    /**
     * list view click listener
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * handles the click on menu items
     *
     * @param position
     */
    public void selectItem(int position) {
        drawerList.setItemChecked(position, true);
        if (this.drawerLayout.isDrawerOpen(this.drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        }
        this.handleMenuItemClick(list.get(position).getMapOption());
    }

    public SKPosition getCurrentPosition() {
        return currentPosition;
    }

    public SKMapSurfaceView getMapView() {
        return mapView;
    }

    public SKMapViewHolder getMapHolder() {
        return mapViewGroup;
    }

    protected void handleMenuItemClick(TestingOption mapOption) {

        switch (mapOption) {
            case ANNOTATION_OPTION:
                DebugSettings.getInstanceForType(AnnotationCustomPoiDebugSettings.class).open(debugBaseLayout, null);
                break;
            case MAP_VIEW_SETTINGS_OPTION:
                DebugSettings.getInstanceForType(MapDebugSettings.class).open(debugBaseLayout, null);
                break;
            case ANIMATION_CUSTOM_VIEW_OPTION:
                DebugSettings.getInstanceForType(CurrentPositionDebugSettings.class).open(debugBaseLayout, null);
                break;
            case MAP_STYLE_OPTION:
                DebugSettings.getInstanceForType(MapStyleDebugSettings.class).open(debugBaseLayout, null);
                break;
            case INTERNALIZATION_OPTION:
                DebugSettings.getInstanceForType(InternationalizationDebugSettings.class).open(debugBaseLayout, null);
                break;
            case SCALE_VIEW_OPTION:
                DebugSettings.getInstanceForType(ScaleViewDebugSettings.class).open(debugBaseLayout, null);
                break;
            case CALLOUT_VIEW_OPTION:
                DebugSettings.getInstanceForType(CalloutViewDebugSettings.class).open(debugBaseLayout, null);
                break;
            case OVERLAYS_OPTION:
                DebugSettings.getInstanceForType(OverlaysDebugSettings.class).open(debugBaseLayout, null);
                break;
            case MAP_VERSION_OPTION:
                DebugSettings.getInstanceForType(VersionInfoDebugSettings.class).open(debugBaseLayout, null);
                break;
            case LAST_RENDERED_FRAME_OPTION:
                DebugSettings.getInstanceForType(ScreenshotDebugSettings.class).open(debugBaseLayout, null);
                break;
            case ANIMATE_OPTION:
                DebugSettings.getInstanceForType(AnimateDebugSettings.class).open(debugBaseLayout, null);
                break;
            case BOUNDING_BOX_OPTION:
                DebugSettings.getInstanceForType(BoundingBoxDebugSettings.class).open(debugBaseLayout, null);
                break;
            case MAP_CACHE_OPTION:
                DebugSettings.getInstanceForType(MapCacheDebugSettings.class).open(debugBaseLayout, null);
                break;
            case POSITION_LOGGING_OPTION:
                DebugSettings.getInstanceForType(PositionLoggingDebugSettings.class).open(debugBaseLayout, null);
                break;
            case POI_TRACKER:
                DebugSettings.getInstanceForType(PoiTrackerDebugSettings.class).open(debugBaseLayout, null);
                break;
            case ROUTING_OPTION:
                DebugSettings.getInstanceForType(RoutingDebugSettings.class).open(debugBaseLayout,null);
                break;
            default:
                break;
        }
    }

    /**
     * Activates the orientation sensor
     */
    private void startOrientationSensor() {
        orientationValues = new float[3];
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Deactivates the orientation sensor
     */
    private void stopOrientationSensor() {
        orientationValues = null;
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    /**
     * @param newCompassValue new z value returned by the sensors
     */
    private void applySmoothAlgorithm(float newCompassValue) {
        if (Math.abs(newCompassValue - currentCompassValue) < 180) {
            currentCompassValue = currentCompassValue + SMOOTH_FACTOR_COMPASS * (newCompassValue - currentCompassValue);
        } else {
            if (currentCompassValue > newCompassValue) {
                currentCompassValue = (currentCompassValue + SMOOTH_FACTOR_COMPASS * ((360 + newCompassValue - currentCompassValue) % 360) + 360) % 360;
            } else {
                currentCompassValue = (currentCompassValue - SMOOTH_FACTOR_COMPASS * ((360 - newCompassValue + currentCompassValue) % 360) + 360) % 360;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //mapView.reportNewHeading(t.values[0]);
        switch (event.sensor.getType()) {

            case Sensor.TYPE_ORIENTATION:
                if (orientationValues != null) {
                    for (int i = 0; i < orientationValues.length; i++) {
                        orientationValues[i] = event.values[i];

                    }
                    if (orientationValues[0] != 0) {
                        if ((System.currentTimeMillis() - lastTimeWhenReceivedGpsSignal) > MINIMUM_TIME_UNTILL_MAP_CAN_BE_UPDATED) {
                            applySmoothAlgorithm(orientationValues[0]);
                            int currentExactScreenOrientation = DebugKitUtils.getExactScreenOrientation(this);
                            if (lastExactScreenOrientation != currentExactScreenOrientation) {
                                lastExactScreenOrientation = currentExactScreenOrientation;
                                switch (lastExactScreenOrientation) {
                                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.PORTRAIT);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.PORTRAIT_UPSIDEDOWN);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.LANDSCAPE_RIGHT);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.LANDSCAPE_LEFT);
                                        break;
                                }
                            }

                            // report to NG the new value
                            if (orientationValues[0] < 0) {
                                mapView.reportNewHeading(-orientationValues[0]);
                            } else {
                                mapView.reportNewHeading(orientationValues[0]);
                            }

                            lastTimeWhenReceivedGpsSignal = System.currentTimeMillis();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runningInstances--;
        if (runningInstances == 0) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
