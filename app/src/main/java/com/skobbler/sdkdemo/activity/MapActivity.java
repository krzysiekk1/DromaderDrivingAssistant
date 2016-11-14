package com.skobbler.sdkdemo.activity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.skobbler.ngx.SKCategories.SKPOICategory;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKAnnotationView;
import com.skobbler.ngx.map.SKBoundingBox;
import com.skobbler.ngx.map.SKCalloutView;
import com.skobbler.ngx.map.SKCircle;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSettings.SKHeadingMode;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKPolygon;
import com.skobbler.ngx.map.SKPolyline;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.map.realreach.SKRealReachListener;
import com.skobbler.ngx.map.realreach.SKRealReachSettings;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationSettings.SKNavigationType;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.poitracker.SKDetectedPOI;
import com.skobbler.ngx.poitracker.SKPOITrackerListener;
import com.skobbler.ngx.poitracker.SKPOITrackerManager;
import com.skobbler.ngx.poitracker.SKTrackablePOI;
import com.skobbler.ngx.poitracker.SKTrackablePOIRule;
import com.skobbler.ngx.poitracker.SKTrackablePOIType;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKRouteSettings.SKRouteMode;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadItem;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadListener;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadManager;
import com.skobbler.ngx.sdktools.onebox.SKOneBoxSearchResult;
import com.skobbler.ngx.sdktools.onebox.listeners.OnListItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.listeners.mOnClickListener;
import com.skobbler.sdkdemo.fragments.OneBoxExtFragment;
import com.skobbler.sdkdemo.navigationui.SKToolsAdvicePlayer;
import com.skobbler.sdkdemo.navigationui.SKToolsNavigationConfiguration;
import com.skobbler.sdkdemo.navigationui.SKToolsNavigationListener;
import com.skobbler.sdkdemo.navigationui.SKToolsNavigationManager;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxFragment;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager;
import com.skobbler.sdkdemo.costs.tolls.TollsCostCalculator;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.ngx.versioning.SKMapVersioningListener;
import com.skobbler.ngx.versioning.SKVersioningManager;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.menu.MenuDrawerAdapter;
import com.skobbler.sdkdemo.application.ApplicationPreferences;
import com.skobbler.sdkdemo.application.DDAApplication;
import com.skobbler.sdkdemo.database.MapDownloadResource;
import com.skobbler.sdkdemo.fragments.MapFragment;
import com.skobbler.sdkdemo.menu.MenuDrawerItem;
import com.skobbler.sdkdemo.util.Utils;
import com.skobbler.sdkdemo.util.PreferenceTypes;

/**
 * Activity displaying the map
 */

public class MapActivity extends Activity implements SKMapSurfaceListener, SKRouteListener, SKNavigationListener,
        SKRealReachListener, SKPOITrackerListener, SKCurrentPositionListener, SensorEventListener,
        SKMapVersioningListener, SKToolsNavigationListener, mOnClickListener {

    private static final byte GREEN_PIN_ICON_ID = 0;

    private static final byte RED_PIN_ICON_ID = 1;

    public static final byte VIA_POINT_ICON_ID = 4;

    private static final String TAG = "MapActivity";

    public ToggleButton toggleButton;

    public static boolean roundTrip;

    /**
     * true, if compass mode is available
     */
    public static boolean compassAvailable;

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

    /**
     * heat maps poi categories
     */
    public static SKPOICategory[] heatMapCategories;

    public enum MapOption {
        MAP_DISPLAY, MAP_STYLES, HEAT_MAP, MAP_CREATOR, MAP_OVERLAYS, ANNOTATIONS, MAP_DOWNLOADS,  MAP_INTERACTION, ALTERNATIVE_ROUTES, REAL_REACH, 
        ROUTING_AND_NAVIGATION, POI_TRACKING, NAVI_UI, SETTINGS, ADDRESS_SEARCH, NEARBY_SEARCH, TOURIST_ATTRACTIONS_SEARCH, REVERSE_GEOCODING, OTHER_SECTION, ONEBOX_SEARCH
    }

    private enum MapAdvices {
        TEXT_TO_SPEECH, AUDIO_FILES
    }

    /**
     * The cunsumption values
     */
    private float[] energyConsumption = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (float) 3.7395504, (float) 4.4476889, (float) 5.4306439, (float) 6.722719,
            (float) 8.2830299, (float) 10.0275093, (float) 11.8820908, (float) 13.799201, (float) 15.751434, (float) 17.7231534, (float) 19.7051378, (float) 21.6916725,
            (float) 23.679014, (float) 25.6645696, (float) 27.6464437, (float) 29.6231796, (float) 31.5936073};

    /**
     * the values returned by magnetic sensor
     */
    private float[] orientationValues;
    /**
     * last time when received GPS signal
     */
    private long lastTimeWhenReceivedGpsSignal;

    /**
     * the current value of the z axis ; at each new step it is updated with the
     * new value
     */
    private float currentCompassValue;

    /**
     * the latest exact screen orientation (given by the
     * getExactScreenOrientation method) that was recorded
     */
    private int lastExactScreenOrientation = -1;

    /**
     * Current option selected
     */
    private MapOption currentMapOption = MapOption.MAP_DISPLAY;

    /**
     * Application context object
     */
    private DDAApplication app;

    /**
     * Surface view for displaying the map
     */
    private SKMapSurfaceView mapView;

    /**
     * View for selecting alternative routes
     */
    private View altRoutesView;

    /**
     * View for selecting the map style
     */
    private LinearLayout mapStylesView;

    /**
     * View for real reach time profile
     */
    private LinearLayout realReachLayout;

    /**
     * Buttons for selecting alternative routes
     */
    private Button[] altRoutesButtons;

    /**
     * Bottom button
     */
    private Button bottomButton;

    /**
     * The current position button
     */
    private Button positionMeButton;

    /**
     * Custom view for adding an annotation
     */
    private RelativeLayout customView;

    /**
     * The heading button
     */
    private Button headingButton;

    /**
     * The map popup view
     */
    private SKCalloutView mapPopup;

    /**
     * Custom callout view title
     */
    private TextView popupTitleView;

    /**
     * Custom callout view description
     */
    private TextView popupDescriptionView;

    /**
     * Ids for alternative routes
     */
    private List<Integer> routeIds = new ArrayList<Integer>();

    /**
     * Tells if a navigation is ongoing
     */
    private boolean navigationInProgress;

    /**
     * Tells if a navigation is ongoing
     */
    private boolean skToolsNavigationInProgress;

    /**
     * counts the consecutive received positions with an accuracy greater than 150
     */
    private byte numberOfConsecutiveBadPositionReceivedDuringNavi;

    /**
     * handler that checks during navigation after every 5 seconds whether a new gps position
     * was received or not
     */
    private Handler gpsPositionsDelayChecker;

    /**
     * Tells if a route calculation is ongoing
     */
    private boolean skToolsRouteCalculated;

    /**
     * POIs to be detected on route
     */
    private Map<Integer, SKTrackablePOI> trackablePOIs;

    /**
     * Trackable POIs that are currently rendered on the map
     */
    private Map<Integer, SKTrackablePOI> drawnTrackablePOIs;

    /**
     * Tracker manager object
     */
    private SKPOITrackerManager poiTrackingManager;

    /**
     * Current position provider
     */
    private SKCurrentPositionProvider currentPositionProvider;

    /**
     * Current position
     */
    private SKPosition currentPosition;

    /**
     * timestamp for the last currentPosition
     */
    private long currentPositionTime;

    /**
     * Tells if heading is currently active
     */
    private boolean headingOn;

    /**
     * Real reach range
     */
    private int realReachRange = 10;

    /**
     * Real reach default vehicle type
     */
    private SKRealReachSettings.SKRealReachVehicleType realReachVehicleType = SKRealReachSettings.SKRealReachVehicleType.CAR;

    /**
     * Real reach default measurement type
     */
    private SKRealReachSettings.SKRealReachMeasurementUnit realReachUnitType = SKRealReachSettings.SKRealReachMeasurementUnit.SECOND;

    /**
     * Real reach connection mode
     */
    private SKRouteSettings.SKRouteConnectionMode skRouteConnectionMode = SKRouteSettings.SKRouteConnectionMode.OFFLINE;


    private ImageButton carButton;

    /**
     * Navigation UI layout
     */
    private static RelativeLayout navigationUI;

    private boolean isStartPointBtnPressed = false, isEndPointBtnPressed = false, isViaPointSelected = false;

    /**
     * The start point(long/lat) for the route.
     */
    private SKCoordinate startPoint;

    /**
     * The destination(long/lat) point for the route
     */
    private SKCoordinate destinationPoint;

    /**
     * The via point(long/lat) for the route
     */
    private SKViaPoint viaPoint;

    /**
     * Text to speech engine
     */
    private TextToSpeech textToSpeechEngine;

    /**
     * manager used for calling methods from SDKTools
     */
    private SKToolsNavigationManager navigationManager;

    /**
     * Drawer layout object
     */
    private DrawerLayout drawerLayout;

    /**
     * The navigation drawer
     */
    private ListView drawerList;

    /**
     * menu items
     */
    private LinkedHashMap<MapOption, MenuDrawerItem> menuItems;
    /**
     * menu items values
     */
    private ArrayList<MenuDrawerItem> list;

    /**
     * the view that holds the map view
     */
    public static SKMapViewHolder mapViewGroup;

    /**
     * Flag for knowing whether the next calculated route should be cached after is calculated
     */
    private boolean shouldCacheTheNextRoute;

    /**
     * The id of the current cached route (if any); null if no route is cached
     */
    private Integer cachedRouteId;

    Toolbar toolbar;
    /**
     * Action bar toggle
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String[] INITIAL_PERMS={Manifest.permission.ACCESS_FINE_LOCATION};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(INITIAL_PERMS, 1337);
        }

        setContentView(R.layout.activity_map);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapFragment mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.onebox_fragment, mapFragment, null);
        fragmentTransaction.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        initializeMenuItems();
    }

    View view;

    public void initialize(View fragmentView) {
        view = fragmentView;
        app = (DDAApplication) getApplication();

        currentPositionProvider = new SKCurrentPositionProvider(this);
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(Utils.hasGpsModule(this), Utils.hasNetworkModule(this), false);

        mapViewGroup = (SKMapViewHolder) view.findViewById(R.id.view_group_map);

        mapViewGroup.setMapSurfaceListener(MapActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mapPopup = mapViewGroup.getCalloutView();
        View layoutView = inflater.inflate(R.layout.layout_popup, null);
        popupTitleView = (TextView) layoutView.findViewById(R.id.top_text);
        popupDescriptionView = (TextView) layoutView.findViewById(R.id.bottom_text);
        mapPopup.setCustomView(layoutView);

        poiTrackingManager = new SKPOITrackerManager(this);
        altRoutesView = view.findViewById(R.id.alt_routes);
        altRoutesButtons =
                new Button[]{(Button) view.findViewById(R.id.alt_route_1), (Button) view.findViewById(R.id.alt_route_2),
                        (Button) view.findViewById(R.id.alt_route_3)};

        mapStylesView = (LinearLayout) view.findViewById(R.id.map_styles);
        bottomButton = (Button) view.findViewById(R.id.bottom_button);
        positionMeButton = (Button) view.findViewById(R.id.position_me_button);
        headingButton = (Button) view.findViewById(R.id.heading_button);

        carButton = (ImageButton) view.findViewById(R.id.real_reach_car_button);

        SKVersioningManager.getInstance().setMapUpdateListener(this);

        toggleButton = (ToggleButton) view.findViewById(R.id.real_reach_round_trip);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    roundTrip = true;
                } else {
                    roundTrip = false;
                }
            }
        });
        realReachLayout = (LinearLayout) view.findViewById(R.id.real_reach_time_layout);
        final TextView realReachTimeText = (TextView) view.findViewById(R.id.real_reach_time);
        final SeekBar realReachSeekBar = (SeekBar) view.findViewById(R.id.real_reach_seekbar);
        realReachSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                realReachRange = progress;

                String unit;
                if (realReachUnitType == SKRealReachSettings.SKRealReachMeasurementUnit.SECOND) {
                    unit = "min";
                } else if (realReachUnitType == SKRealReachSettings.SKRealReachMeasurementUnit.METER) {
                    unit = "km";
                } else {
                    unit = "%";
                }

                realReachTimeText.setText(realReachRange + " " + unit);
                showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);

            }
        });


        Spinner spinner = (Spinner) view.findViewById(R.id.real_reach_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.real_reach_measurement_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String unit = (String) parent.getItemAtPosition(position);
                realReachSeekBar.setProgress(10);
                if (unit.equals(getString(R.string.real_reach_profile_distance)) || unit.equals(getString(R.string.real_reach_profile_time))) {

                    realReachVehicleType = SKRealReachSettings.SKRealReachVehicleType.CAR;
                    carButton.setBackgroundColor(getResources().getColor(R.color.blue_filling));
                    findViewById(R.id.real_reach_vehicle_layout).setVisibility(View.VISIBLE);

                    if (unit.equals(getString(R.string.real_reach_profile_distance))) {
                        realReachUnitType = SKRealReachSettings.SKRealReachMeasurementUnit.METER;
                        realReachSeekBar.setMax(30);
                        showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                    } else if (unit.equals(getString(R.string.real_reach_profile_time))) {
                        realReachUnitType = SKRealReachSettings.SKRealReachMeasurementUnit.SECOND;
                        realReachSeekBar.setMax(60);
                        showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                realReachUnitType = SKRealReachSettings.SKRealReachMeasurementUnit.SECOND;
                realReachVehicleType = SKRealReachSettings.SKRealReachVehicleType.CAR;
                realReachSeekBar.setMax(60);
                realReachSeekBar.setProgress(10);
                findViewById(R.id.real_reach_vehicle_layout).setVisibility(View.VISIBLE);
                showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
            }
        });

        Spinner spinnerOnOfHy = (Spinner) view.findViewById(R.id.real_reach_online_offline_hybrid);
        ArrayAdapter<CharSequence> adapterOnOfHy = ArrayAdapter.createFromResource(this, R.array.real_reach_online_offline_hybrid, android.R.layout.simple_spinner_item);
        adapterOnOfHy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOnOfHy.setAdapter(adapterOnOfHy);
        spinnerOnOfHy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String unit = (String) parent.getItemAtPosition(position);
                realReachSeekBar.setProgress(10);

                if (unit.equals(getString(R.string.real_reach_online)) || unit.equals(getString(R.string.real_reach_offline))) {
                    if (unit.equals(getString(R.string.real_reach_online))) {
                        skRouteConnectionMode = SKRouteSettings.SKRouteConnectionMode.ONLINE;
                        showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                    } else if (unit.equals(getString(R.string.real_reach_offline))) {
                        skRouteConnectionMode = SKRouteSettings.SKRouteConnectionMode.OFFLINE;
                        showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                    }

                } else {
                    skRouteConnectionMode = SKRouteSettings.SKRouteConnectionMode.HYBRID;
                    showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                realReachUnitType = SKRealReachSettings.SKRealReachMeasurementUnit.SECOND;
                realReachVehicleType = SKRealReachSettings.SKRealReachVehicleType.CAR;
                realReachSeekBar.setMax(60);
                realReachSeekBar.setProgress(10);
                findViewById(R.id.real_reach_vehicle_layout).setVisibility(View.VISIBLE);
                showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
            }
        });

        navigationUI = (RelativeLayout) view.findViewById(R.id.navigation_ui_layout);
        initializeTrackablePOIs();
    }


    /**
     * Initializes the navigation drawer list items
     */
    public void initializeMenuItems() {
        menuItems = new LinkedHashMap<MapOption, MenuDrawerItem>();

        menuItems.put(MapOption.NAVI_UI, create(MapOption.NAVI_UI, getString(R.string.option_map), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.ONEBOX_SEARCH, create(MapOption.ONEBOX_SEARCH, getResources().getString(R.string.option_onebox), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.TOURIST_ATTRACTIONS_SEARCH, create(MapOption.TOURIST_ATTRACTIONS_SEARCH, getResources().getString(R.string.option_tourist_attractions_search), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.MAP_DOWNLOADS, create(MapOption.MAP_DOWNLOADS, getResources().getString(R.string.option_map_downloads), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.SETTINGS, create(MapOption.SETTINGS, getResources().getString(R.string.option_settings), MenuDrawerItem.ITEM_TYPE));

        menuItems.put(MapOption.OTHER_SECTION, create(MapOption.OTHER_SECTION, getResources().getString(R.string.other_section).toUpperCase(), MenuDrawerItem.SECTION_TYPE));

        menuItems.put(MapOption.MAP_DISPLAY, create(MapOption.MAP_DISPLAY, getResources().getString(R.string.option_map_display), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.MAP_STYLES, create(MapOption.MAP_STYLES, getResources().getString(R.string.option_map_styles), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.HEAT_MAP, create(MapOption.HEAT_MAP, getResources().getString(R.string.option_heat_map), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.MAP_CREATOR, create(MapOption.MAP_CREATOR, getResources().getString(R.string.option_map_creator), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.MAP_OVERLAYS, create(MapOption.MAP_OVERLAYS, getResources().getString(R.string.option_overlays), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.ANNOTATIONS, create(MapOption.ANNOTATIONS, getResources().getString(R.string.option_annotations), MenuDrawerItem.ITEM_TYPE));
        if (Utils.isMultipleMapSupportEnabled) {
            menuItems.put(MapOption.MAP_INTERACTION, create(MapOption.MAP_INTERACTION, getResources().getString(R.string.option_other_map), MenuDrawerItem.ITEM_TYPE));
        }

//        menuItems.put(MapOption.NAVIGATION_SECTION, create(MapOption.NAVIGATION_SECTION, getResources().getString(R.string.options_group_navigation).toUpperCase(), MenuDrawerItem.SECTION_TYPE));
        menuItems.put(MapOption.ROUTING_AND_NAVIGATION, create(MapOption.ROUTING_AND_NAVIGATION, getResources().getString(R.string.option_routing_and_navigation), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.ALTERNATIVE_ROUTES, create(MapOption.ALTERNATIVE_ROUTES, getResources().getString(R.string.option_alternative_routes), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.REAL_REACH, create(MapOption.REAL_REACH, getResources().getString(R.string.option_real_reach), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.POI_TRACKING, create(MapOption.POI_TRACKING, getResources().getString(R.string.option_poi_tracking), MenuDrawerItem.ITEM_TYPE));

//        menuItems.put(MapOption.SEARCHES_SECTION, create(MapOption.SEARCHES_SECTION, getResources().getString(R.string.search).toUpperCase(), MenuDrawerItem.SECTION_TYPE));
        menuItems.put(MapOption.ADDRESS_SEARCH, create(MapOption.ADDRESS_SEARCH, getResources().getString(R.string.option_address_search), MenuDrawerItem.ITEM_TYPE));
        menuItems.put(MapOption.NEARBY_SEARCH, create(MapOption.NEARBY_SEARCH, getResources().getString(R.string.option_nearby_search), MenuDrawerItem.ITEM_TYPE));


        menuItems.put(MapOption.REVERSE_GEOCODING, create(MapOption.REVERSE_GEOCODING, getResources().getString(R.string.option_reverse_geocoding), MenuDrawerItem.ITEM_TYPE));

        list = new ArrayList<MenuDrawerItem>(menuItems.values());

        drawerList.setAdapter(new MenuDrawerAdapter(this, R.layout.element_menu_drawer_item, list));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    /**
     * Creates menu drawer item(section item/list item)
     *
     * @param mapOption
     * @param label
     * @param itemType
     * @return
     */
    public static MenuDrawerItem create(MapOption mapOption, String label, int itemType) {
        MenuDrawerItem menuDrawerItem = new MenuDrawerItem(mapOption);
        menuDrawerItem.setLabel(label);
        menuDrawerItem.setItemType(itemType);
        return menuDrawerItem;
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
     * Populate the collection of trackable POIs
     */
    private void initializeTrackablePOIs() {

        trackablePOIs = new HashMap<Integer, SKTrackablePOI>();

        trackablePOIs.put(64142, new SKTrackablePOI(64142, 0, 37.735610, -122.446434, -1, "Teresita Boulevard"));
        trackablePOIs.put(64143, new SKTrackablePOI(64143, 0, 37.732367, -122.442033, -1, "Congo Street"));
        trackablePOIs.put(64144, new SKTrackablePOI(64144, 0, 37.732237, -122.429190, -1, "John F Foran Freeway"));
        trackablePOIs.put(64145, new SKTrackablePOI(64145, 1, 37.738090, -122.401470, -1, "Revere Avenue"));
        trackablePOIs.put(64146, new SKTrackablePOI(64146, 0, 37.741128, -122.398562, -1, "McKinnon Ave"));
        trackablePOIs.put(64147, new SKTrackablePOI(64147, 1, 37.746154, -122.394077, -1, "Evans Ave"));
        trackablePOIs.put(64148, new SKTrackablePOI(64148, 0, 37.750057, -122.392287, -1, "Cesar Chavez Street"));
        trackablePOIs.put(64149, new SKTrackablePOI(64149, 1, 37.762823, -122.392957, -1, "18th Street"));
        trackablePOIs.put(64150, new SKTrackablePOI(64150, 0, 37.760242, -122.392495, 180, "20th Street"));
        trackablePOIs.put(64151, new SKTrackablePOI(64151, 0, 37.755157, -122.392196, 180, "23rd Street"));

        trackablePOIs.put(64152, new SKTrackablePOI(64152, 0, 37.773526, -122.452706, -1, "Shrader Street"));
        trackablePOIs.put(64153, new SKTrackablePOI(64153, 0, 37.786535, -122.444528, -1, "Pine Street"));
        trackablePOIs.put(64154, new SKTrackablePOI(64154, 1, 37.792242, -122.424426, -1, "Franklin Street"));
        trackablePOIs.put(64155, new SKTrackablePOI(64155, 0, 37.716146, -122.409480, -1, "Campbell Ave"));
        trackablePOIs.put(64156, new SKTrackablePOI(64156, 0, 37.719133, -122.388280, -1, "Fitzgerald Ave"));

        drawnTrackablePOIs = new HashMap<Integer, SKTrackablePOI>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapViewGroup.onResume();

        if (headingOn) {
            startOrientationSensor();
        }

        if (currentMapOption == MapOption.NAVI_UI) {
            final ToggleButton selectStartPointBtn = (ToggleButton) findViewById(R.id.select_start_point_button);
            selectStartPointBtn.setVisibility(View.GONE);
        }

        if (Utils.isMultipleMapSupportEnabled == false && currentMapOption == MapOption.HEAT_MAP && heatMapCategories != null) {
            mapView.showHeatMapsWithPoiType(heatMapCategories);
        }

        if (currentMapOption == MapOption.MAP_INTERACTION && isRouteCached()) {
            loadRouteFromCache();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewGroup.onPause();

        if (headingOn) {
            stopOrientationSensor();
        }
        if (compassAvailable) {
            stopOrientationSensor();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentPositionProvider.stopLocationUpdates();
        SKMaps.getInstance().destroySKMaps();
        if (textToSpeechEngine != null) {
            textToSpeechEngine.stop();
            textToSpeechEngine.shutdown();
        }
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder mapHolder) {
        View chessBackground = view.findViewById(R.id.chess_board_background);
        chessBackground.setVisibility(View.GONE);
        mapView = mapViewGroup.getMapSurfaceView();
        applySettingsOnMapView();
        if (SplashActivity.newMapVersionDetected != 0) {
            showUpdateDialog(SplashActivity.newMapVersionDetected);
        }

        if (!navigationInProgress) {
            mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.NONE);
        }

        if (Utils.isMultipleMapSupportEnabled == true && currentMapOption == MapOption.HEAT_MAP && heatMapCategories != null) {
            mapView.showHeatMapsWithPoiType(heatMapCategories);
        }

        if (currentPosition != null) {
            SKPositionerManager.getInstance().reportNewGPSPosition(currentPosition);
        }


    }

    @Override
    public void onBoundingBoxImageRendered(int i) {
    }


    @Override
    public void onGLInitializationError(String messsage) {
    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && !skToolsNavigationInProgress && !skToolsRouteCalculated) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @SuppressLint("ResourceAsColor")
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.alt_route_1:
                selectAlternativeRoute(0);
                break;
            case R.id.alt_route_2:
                selectAlternativeRoute(1);
                break;
            case R.id.alt_route_3:
                selectAlternativeRoute(2);
                break;
            case R.id.map_style_day:
                selectMapStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "daystyle/", "daystyle.json"));
                break;
            case R.id.map_style_night:
                selectMapStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "nightstyle/", "nightstyle.json"));
                break;
            case R.id.map_style_outdoor:
                selectMapStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "outdoorstyle/", "outdoorstyle.json"));
                break;
            case R.id.map_style_grayscale:
                selectMapStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "grayscalestyle/",
                        "grayscalestyle.json"));
                break;
//            case R.id.custom_toast:
//                showMessage(getString(R.string.something), R.drawable.toast_icon_refresh, "RETRY", null);
//                break;
            case R.id.bottom_button:
                if (currentMapOption == MapOption.ROUTING_AND_NAVIGATION)  {
                    if (bottomButton.getText().equals(getResources().getString(R.string.calculate_route))) {
                        launchRouteCalculation(new SKCoordinate(19.948295, 50.007004), new SKCoordinate(21.016957, 52.218425));
                    } else if (bottomButton.getText().equals(getResources().getString(R.string.start_navigation))) {
                        new AlertDialog.Builder(this)
                                .setMessage("Choose the advice type")
                                .setCancelable(false)
                                .setPositiveButton("Scout audio", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        bottomButton.setText(getResources().getString(R.string.stop_navigation));
                                        setAdvicesAndStartNavigation(MapAdvices.AUDIO_FILES);
                                    }
                                })
                                .setNegativeButton("Text to speech", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (textToSpeechEngine == null) {
                                            Toast.makeText(MapActivity.this, "Initializing TTS engine",
                                                    Toast.LENGTH_LONG).show();
                                            textToSpeechEngine = new TextToSpeech(MapActivity.this,
                                                    new TextToSpeech.OnInitListener() {
                                                        @Override
                                                        public void onInit(int status) {
                                                            if (status == TextToSpeech.SUCCESS) {
                                                                int result = textToSpeechEngine.setLanguage(Locale.ENGLISH);
                                                                if (result == TextToSpeech.LANG_MISSING_DATA || result ==
                                                                        TextToSpeech.LANG_NOT_SUPPORTED) {
                                                                    Toast.makeText(MapActivity.this,
                                                                            "This Language is not supported",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {
                                                                Toast.makeText(MapActivity.this, getString(R.string.text_to_speech_engine_not_initialized),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                            bottomButton.setText(getResources().getString(R.string
                                                                    .stop_navigation));
                                                            setAdvicesAndStartNavigation(MapAdvices.TEXT_TO_SPEECH);
                                                        }
                                                    });
                                        } else {
                                            bottomButton.setText(getResources().getString(R.string.stop_navigation));
                                            setAdvicesAndStartNavigation(MapAdvices.TEXT_TO_SPEECH);
                                        }

                                    }
                                })
                                .show();
                        bottomButton.setText(getResources().getString(R.string.stop_navigation));
                    } else if (bottomButton.getText().equals(getResources().getString(R.string.stop_navigation))) {
                        stopNavigation();
                        bottomButton.setText(getResources().getString(R.string.start_navigation));
                    }
                } else if (currentMapOption == MapOption.MAP_INTERACTION) {
                    Toast.makeText(MapActivity.this, "New map instance created", Toast.LENGTH_LONG).show();
                    SKRouteManager.getInstance().clearCurrentRoute();
                    Intent intent = new Intent(MapActivity.this, MapCacheActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.position_me_button:
                if (headingOn) {
                    setHeading(false);
                }
                if (mapView != null && currentPosition != null) {
                    mapView.centerOnCurrentPosition(17, true, 500);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_position_available), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.heading_button:
                if (currentPosition != null) {
                    setHeading(true);

                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_position_available), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.real_reach_car_button:
                realReachVehicleType = SKRealReachSettings.SKRealReachVehicleType.CAR;
                showRealReach(realReachUnitType, realReachVehicleType, realReachRange, skRouteConnectionMode);
                carButton.setBackgroundColor(getResources().getColor(R.color.blue_filling));
                break;
            case R.id.navigation_ui_back_button:
                Button backButton = (Button) findViewById(R.id.navigation_ui_back_button);
                LinearLayout naviButtons = (LinearLayout) findViewById(R.id.navigation_ui_buttons);
                if (backButton.getText().equals(">")) {
                    naviButtons.setVisibility(View.VISIBLE);
                    backButton.setText("<");
                } else {
                    naviButtons.setVisibility(View.GONE);
                    backButton.setText(">");
                }
                break;
            case R.id.calculate_routes_button:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getActionBar().setHomeButtonEnabled(false);
                getActionBar().setDisplayHomeAsUpEnabled(false);

                calculateRouteFromSKTools();
                break;

            case R.id.clear_via_point_button:
                viaPoint = null;
                mapView.deleteAnnotation(VIA_POINT_ICON_ID);
                findViewById(R.id.clear_via_point_button).setVisibility(View.GONE);
                break;
            case R.id.position_me_navigation_ui_button:
                if (currentPosition != null) {
                    mapView.centerOnCurrentPosition(15, true, 1000);
                    mapView.getMapSettings().setOrientationIndicatorType(
                            SKMapSurfaceView.SKOrientationIndicatorType.DEFAULT);
                    mapView.getMapSettings()
                            .setHeadingMode(SKMapSettings.SKHeadingMode.NONE);
                } else {
                    Toast.makeText(MapActivity.this,
                            getString(R.string.no_position_available),
                            Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }



    private void testingAlertDialog(){
        DialogMessage dm = new DialogMessage(this, view);
        dm.setMessage("cokolwiek", R.string.mes1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MapActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
            }
        }, R.string.mes2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    } });

        dm.showWithTimeout(5000);

    }


    public void calculateRouteFromSKTools() {

        SKToolsNavigationConfiguration configuration = new SKToolsNavigationConfiguration();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //set route type
        String prefRouteType = "0";
            prefRouteType = sharedPreferences.getString(PreferenceTypes.K_ROUTE_TYPE,
                    "2");
            if (prefRouteType.equals("0")) {
                configuration.setRouteType(SKRouteMode.CAR_SHORTEST);
            } else if (prefRouteType.equals("1")) {
                configuration.setRouteType(SKRouteMode.CAR_FASTEST);
            } else if (prefRouteType.equals("2")) {
                configuration.setRouteType(SKRouteMode.EFFICIENT);
            }

        boolean tollRoads = sharedPreferences.getBoolean(PreferenceTypes.K_AVOID_TOLLS, false);
        if (tollRoads) {
            configuration.setTollRoadsAvoided(true);
        }

        configuration.setNavigationType(SKNavigationType.REAL);
         if (currentPosition == null) {
             showNoCurrentPosDialog();
             return;
          }
         startPoint = currentPosition.getCoordinate();
 
         // set distance format
         configuration.setDistanceUnitType(SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
 
         // set speed warnings
         configuration.setSpeedWarningThresholdInCity(20.0);
         configuration.setSpeedWarningThresholdOutsideCity(20.0);
 
         // other settings
         configuration.setAutomaticDayNight(true);
         configuration.setFerriesAvoided(false);
         configuration.setHighWaysAvoided(false);
         configuration.setContinueFreeDriveAfterNavigationEnd(true);       
        navigationUI.setVisibility(View.GONE);
        configuration.setStartCoordinate(startPoint);
        configuration.setDestinationCoordinate(destinationPoint);
        List<SKViaPoint> viaPointList = new ArrayList<SKViaPoint>();
        if (viaPoint != null) {
            viaPointList.add(viaPoint);
            configuration.setViaPointCoordinateList(viaPointList);
        }
        configuration.setDayStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "daystyle/",
                "daystyle.json"));
        configuration.setNightStyle(new SKMapViewStyle(app.getMapResourcesDirPath() + "nightstyle/",
                "nightstyle.json"));
        navigationManager = new SKToolsNavigationManager(this, R.id.map_layout_root);
        navigationManager.setNavigationListener(this);

        if (configuration.getStartCoordinate() != null && configuration.getDestinationCoordinate() != null) {
            navigationManager.launchRouteCalculation(configuration, mapViewGroup);
        }
    }

    /**
     * Initializes navigation UI menu
     *
     * @param showStartingAndDestinationAnnotations
     */
    private void initializeNavigationUI(boolean showStartingAndDestinationAnnotations) {
        final ToggleButton selectViaPointBtn = (ToggleButton) findViewById(R.id.select_via_point_button);
        final ToggleButton selectStartPointBtn = (ToggleButton) findViewById(R.id.select_start_point_button);
        final ToggleButton selectEndPointBtn = (ToggleButton) findViewById(R.id.select_end_point_button);

        selectStartPointBtn.setVisibility(View.GONE);
        /*SKPosition currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(true);
        SKCoordinate currentCoordinate = currentPosition.getCoordinate();
        startPoint = currentCoordinate;*/
        startPoint = new SKCoordinate(50.007004, 19.948295);
        destinationPoint = new SKCoordinate(21.016957, 52.218425);
        if (showStartingAndDestinationAnnotations) {
            SKAnnotation annotation = new SKAnnotation(GREEN_PIN_ICON_ID);
            annotation
                    .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            annotation.setLocation(startPoint);
            mapView.addAnnotation(annotation,
                    SKAnimationSettings.ANIMATION_NONE);

            annotation = new SKAnnotation(RED_PIN_ICON_ID);
            annotation
                    .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
            annotation.setLocation(destinationPoint);
            mapView.addAnnotation(annotation,
                    SKAnimationSettings.ANIMATION_NONE);

        }
        mapView.setZoom(11);
        mapView.animateToLocation(startPoint, 0);

        selectStartPointBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isStartPointBtnPressed = true;
                    isEndPointBtnPressed = false;
                    isViaPointSelected = false;
                    selectEndPointBtn.setChecked(false);
                    selectViaPointBtn.setChecked(false);
                    Toast.makeText(MapActivity.this, getString(R.string.long_tap_for_position),
                            Toast.LENGTH_LONG).show();
                } else {
                    isStartPointBtnPressed = false;
                }
            }
        });
        selectEndPointBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEndPointBtnPressed = true;
                    isStartPointBtnPressed = false;
                    isViaPointSelected = false;
                    selectStartPointBtn.setChecked(false);
                    selectViaPointBtn.setChecked(false);
                    Toast.makeText(MapActivity.this, getString(R.string.long_tap_for_position),
                            Toast.LENGTH_LONG).show();
                } else {
                    isEndPointBtnPressed = false;
                }
            }
        });

        selectViaPointBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isViaPointSelected = true;
                    isStartPointBtnPressed = false;
                    isEndPointBtnPressed = false;
                    selectStartPointBtn.setChecked(false);
                    selectEndPointBtn.setChecked(false);
                    Toast.makeText(MapActivity.this, getString(R.string.long_tap_for_position),
                            Toast.LENGTH_LONG).show();
                } else {
                    isViaPointSelected = false;
                }
            }
        });

        navigationUI.setVisibility(View.VISIBLE);
    }


    private void initializeOneBox() {
        if(currentPosition != null){
            OneBoxManager.setCurrentPosition(currentPosition.getCoordinate());
        }
        getActionBar().hide();

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        OneBoxExtFragment OneBoxExtFragment = new OneBoxExtFragment();
        fragmentTransaction.add(R.id.onebox_fragment, OneBoxExtFragment, OneBoxManager.ONEBOX_FRAGMENT_ID);
        fragmentTransaction.addToBackStack(OneBoxManager.ONEBOX_FRAGMENT_ID);
        fragmentTransaction.commit();
        ((OneBoxExtFragment)getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).ONEBOX_ACTIVATED = true;
    }

    private void showNoCurrentPosDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
        alert.setMessage("There is no current position available");
        alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alert.show();
    }

    /**
     * Launches a single route calculation
     */
    /**
     * Launches a single route calculation
     */
    public void launchRouteCalculation(SKCoordinate startPoint, SKCoordinate destinationPoint) {
        clearRouteFromCache();
        // get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();
        // set start and destination points
        route.setStartCoordinate(startPoint);
        route.setDestinationCoordinate(destinationPoint);
        // set the number of routes to be calculated
        route.setMaximumReturnedRoutes(1);
        // set the route mode
        route.setRouteMode(SKRouteMode.CAR_FASTEST);
        // set whether the route should be shown on the map after it's computed
        route.setRouteExposed(true);
        route.setRequestAdvices(true);
        route.setRequestCountryCodes(true);
        route.setRequestExtendedPoints(true);
        // set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(this);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
    }

    /**
     * Launches the calculation of three alternative routes
     */
    private void launchAlternativeRouteCalculation() {
        SKRouteSettings route = new SKRouteSettings();
        route.setStartCoordinate(new SKCoordinate(37.787189, -122.392284));
        route.setDestinationCoordinate(new SKCoordinate(37.856300, -122.484378));
        // number of alternative routes specified here
        route.setMaximumReturnedRoutes(3);
        route.setRouteMode(SKRouteMode.CAR_FASTEST);
        route.setRouteExposed(true);
        route.setRequestAdvices(true);
        route.setRequestCountryCodes(true);
        route.setRequestExtendedPoints(true);
        SKRouteManager.getInstance().setRouteListener(this);
        SKRouteManager.getInstance().calculateRoute(route);
    }

    /**
     * Draws annotations on map
     */
    private void prepareAnnotations() {

        // Add annotation using texture ID - from the json files.
        // get the annotation object
        SKAnnotation annotationWithTextureId = new SKAnnotation(10);
        // set annotation location
        annotationWithTextureId.setLocation(new SKCoordinate(37.7765, -122.4200));
        // set minimum zoom level at which the annotation should be visible
        annotationWithTextureId.setMininumZoomLevel(5);
        // set the annotation's type
        annotationWithTextureId.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_BLUE);
        // render annotation on map
        mapView.addAnnotation(annotationWithTextureId, SKAnimationSettings.ANIMATION_NONE);

        // // add an annotation with a view
        SKAnnotation annotationFromView = new SKAnnotation(11);
        annotationFromView.setLocation(new SKCoordinate(37.761349, -122.423573));
        annotationFromView.setMininumZoomLevel(5);
        SKAnnotationView annotationView = new SKAnnotationView();
        customView =
                (RelativeLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.layout_custom_view, null, false);
        //  If width and height of the view  are not power of 2 the actual size of the image will be the next power of 2 of max(width,height).
        annotationView.setView(findViewById(R.id.customView));
        annotationFromView.setAnnotationView(annotationView);
        mapView.addAnnotation(annotationFromView, SKAnimationSettings.ANIMATION_NONE);

        // set map zoom level
        mapView.setZoom(13);
        // center map on a position
        mapView.animateToLocation(new SKCoordinate(37.7765, -122.4200), 1000);
    }

    /**
     * Draws shapes on map
     */
    private void drawShapes() {

        // get a polygon shape object
        SKPolygon polygon = new SKPolygon();
        // set the polygon's nodes
        List<SKCoordinate> nodes = new ArrayList<SKCoordinate>();
        nodes.add(new SKCoordinate(37.7765, -122.4342));
        nodes.add(new SKCoordinate(37.7765, -122.4141));
        nodes.add(new SKCoordinate(37.7620, -122.4342));
        polygon.setNodes(nodes);
        // set the outline size
        polygon.setOutlineSize(3);
        // set colors used to render the polygon
        polygon.setOutlineColor(new float[]{1f, 0f, 0f, 1f});
        polygon.setColor(new float[]{1f, 0f, 0f, 0.2f});
        polygon.setIdentifier(10);
        // render the polygon on the map
        mapView.addPolygon(polygon);

        // get a circle mask shape object
        SKCircle circleMask = new SKCircle();
        // set the shape's mask scale
        circleMask.setMaskedObjectScale(1.3f);
        // set the colors
        circleMask.setColor(new float[]{1f, 1f, 0.5f, 0.67f});
        circleMask.setOutlineColor(new float[]{0f, 0f, 0f, 1f});
        circleMask.setOutlineSize(3);
        // set circle center and radius
        circleMask.setCircleCenter(new SKCoordinate(37.7665,-122.4200));
        circleMask.setRadius(300);
        // set outline properties
        circleMask.setOutlineDottedPixelsSkip(6);
        circleMask.setOutlineDottedPixelsSolid(10);
        // set the number of points for rendering the circle
        circleMask.setNumberOfPoints(150);
        circleMask.setIdentifier(11);
        // render the circle mask
        mapView.addCircle(circleMask);

        // get a polyline object
        SKPolyline polyline = new SKPolyline();
        // set the nodes on the polyline
        nodes = new ArrayList<SKCoordinate>();
        nodes.add(new SKCoordinate(37.7898, -122.4342));
        nodes.add(new SKCoordinate(37.7898, -122.4141));
        nodes.add(new SKCoordinate(37.7753, -122.4342));
        polyline.setNodes(nodes);
        // set polyline color
        polyline.setColor(new float[]{0f, 0f, 1f, 1f});
        // set properties for the outline
        polyline.setOutlineColor(new float[]{0f, 0f, 1f, 1f});
        polyline.setOutlineSize(4);
        polyline.setOutlineDottedPixelsSolid(3);
        polyline.setOutlineDottedPixelsSkip(3);
        polyline.setIdentifier(12);
        mapView.addPolyline(polyline);
    }

    private void selectMapStyle(SKMapViewStyle newStyle) {
        mapView.getMapSettings().setMapStyle(newStyle);
        selectStyleButton();
    }

    /**
     * Selects the style button for the current map style
     */
    private void selectStyleButton() {
        for (int i = 0; i < mapStylesView.getChildCount(); i++) {
            mapStylesView.getChildAt(i).setSelected(false);
        }
        SKMapViewStyle mapStyle = mapView.getMapSettings().getMapStyle();
        if (mapStyle == null || mapStyle.getStyleFileName().equals("daystyle.json")) {
            findViewById(R.id.map_style_day).setSelected(true);
        } else if (mapStyle.getStyleFileName().equals("nightstyle.json")) {
            findViewById(R.id.map_style_night).setSelected(true);
        } else if (mapStyle.getStyleFileName().equals("outdoorstyle.json")) {
            findViewById(R.id.map_style_outdoor).setSelected(true);
        } else if (mapStyle.getStyleFileName().equals("grayscalestyle.json")) {
            findViewById(R.id.map_style_grayscale).setSelected(true);
        }
    }

    /**
     * Clears the map
     */
    private void clearMap() {
        setHeading(false);
        switch (currentMapOption) {
            case MAP_DISPLAY:
                break;
            case MAP_OVERLAYS:
                // clear all map overlays (shapes)
                mapView.clearAllOverlays();
                break;
            case ALTERNATIVE_ROUTES:
                hideAlternativeRoutesButtons();
                // clear the alternative routes
                SKRouteManager.getInstance().clearRouteAlternatives();
                // clear the selected route
                SKRouteManager.getInstance().clearCurrentRoute();
                routeIds.clear();
                break;
            case MAP_STYLES:
                mapStylesView.setVisibility(View.GONE);
                break;
            case REAL_REACH:
                // removes real reach from the map
                mapView.clearRealReachDisplay();
                realReachLayout.setVisibility(View.GONE);
                Spinner spinner = (Spinner) findViewById(R.id.real_reach_spinner);
                spinner.setSelection(0);
                break;
            case ANNOTATIONS:
                mapPopup.setVisibility(View.GONE);
                // removes the annotations and custom POIs currently rendered
                mapView.deleteAllAnnotationsAndCustomPOIs();
            case ROUTING_AND_NAVIGATION:
                bottomButton.setVisibility(View.GONE);
                SKRouteManager.getInstance().clearCurrentRoute();
                mapView.deleteAllAnnotationsAndCustomPOIs();
                if (navigationInProgress) {
                    // stop navigation if ongoing
                    stopNavigation();
                }
                break;
            case POI_TRACKING:
                if (navigationInProgress) {
                    // stop the navigation
                    stopNavigation();
                }
                SKRouteManager.getInstance().clearCurrentRoute();
                // remove the detected POIs from the map
                mapView.deleteAllAnnotationsAndCustomPOIs();
                // stop the POI tracker
                poiTrackingManager.stopPOITracker();
                break;
            case HEAT_MAP:
                heatMapCategories = null;
                mapView.clearHeatMapsDisplay();
                break;
            case NAVI_UI:
            case MAP_INTERACTION:
                mapView.deleteAllAnnotationsAndCustomPOIs();
                bottomButton.setVisibility(View.GONE);
                SKRouteManager.getInstance().clearCurrentRoute();
                clearRouteFromCache();
                shouldCacheTheNextRoute = false;
            default:
                break;
        }
        currentMapOption = MapOption.MAP_DISPLAY;
        positionMeButton.setVisibility(View.VISIBLE);
        headingButton.setVisibility(View.VISIBLE);
    }

    private void deselectAlternativeRoutesButtons() {
        for (Button b : altRoutesButtons) {
            b.setSelected(false);
        }
    }

    private void hideAlternativeRoutesButtons() {
        deselectAlternativeRoutesButtons();
        altRoutesView.setVisibility(View.GONE);
        for (Button b : altRoutesButtons) {
            b.setText("distance\ntime");
        }
    }

    private void selectAlternativeRoute(int routeIndex) {
        if (routeIds.size() > routeIndex) {
            deselectAlternativeRoutesButtons();
            altRoutesButtons[routeIndex].setSelected(true);
            SKRouteManager.getInstance().zoomToRoute(1, 1, 110, 8, 8, 8, 0);
            SKRouteManager.getInstance().setCurrentRouteByUniqueId(routeIds.get(routeIndex));
        }

    }

    /**
     * Launches a navigation on the current route
     */
    private void launchNavigation() {
        // get navigation settings object
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        // set the desired navigation settings
        navigationSettings.setNavigationType(SKNavigationType.SIMULATION);
        navigationSettings.setPositionerVerticalAlignment(-0.25f);
        navigationSettings.setShowRealGPSPositions(false);
        // get the navigation manager object
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        // set listener for navigation events
        navigationManager.setNavigationListener(this);

        // start navigating using the settings
        navigationManager.startNavigation(navigationSettings);
        navigationInProgress = true;
    }

    /**
     * Setting the audio advices
     */
    private void setAdvicesAndStartNavigation(MapAdvices currentMapAdvices) {
        final SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorConfigPath(app.getMapResourcesDirPath() + "/Advisor");
        advisorSettings.setResourcePath(app.getMapResourcesDirPath() + "/Advisor/Languages");
        advisorSettings.setAdvisorVoice("en");
        switch (currentMapAdvices) {
            case AUDIO_FILES:
                advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
                break;
            case TEXT_TO_SPEECH:
                advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.TEXT_TO_SPEECH);
                break;
        }
        SKRouteManager.getInstance().setAdvisorSettings(advisorSettings);
        launchNavigation();

    }


    /**
     * Stops the navigation
     */
    private void stopNavigation() {
        navigationInProgress = false;
        routeIds.clear();
        if (textToSpeechEngine != null && !textToSpeechEngine.isSpeaking()) {
            textToSpeechEngine.stop();
        }

        SKNavigationManager.getInstance().stopNavigation();

    }

    // route computation callbacks ...
    @Override
    public void onAllRoutesCompleted() {
        if (shouldCacheTheNextRoute) {
            shouldCacheTheNextRoute = false;
            SKRouteManager.getInstance().saveRouteToCache(cachedRouteId);
        }
        //SKRouteManager.getInstance().zoomToRoute(1, 1, 8, 8, 8, 8, 0);
        if (currentMapOption == MapOption.POI_TRACKING) {

            // set warning rules for trackable POIs
            // SKTrackablePOIType myType =SKTrackablePOIType.forInt(1000);
            SKTrackablePOIRule rule = new SKTrackablePOIRule();
            rule.setAerialDistance(5000); // this would be our main constraint, stating that all the POIs with 5000m, aerial distance should be detected
            rule.setNumberOfTurns(100); // this has to be increased - otherwise some points will be disconsidered
            rule.setRouteDistance(10000);//this has to be increased as the real road route will be longer than the aerial distance
            rule.setMinSpeedIgnoreDistanceAfterTurn(20); //decrease this to evaluate all candidates
            rule.setMaxDistanceAfterTurn(10000); //increase this to make sure we don't exclude any candidates
            rule.setEliminateIfUTurn(false); // setting this to true (default) excludes points that require us to make an U-turn to get to them
            rule.setPlayAudioWarning(false);
            poiTrackingManager.setRuleForPOIType(SKTrackablePOIType.SPEEDCAM.getValue(), rule);
            // start the POI tracker
            poiTrackingManager.startPOITrackerWithRadius(10000, 0.5);
            // launch navigation
            launchNavigation();
        }
    }


    @Override
    public void onReceivedPOIs(SKTrackablePOIType type, List<SKDetectedPOI> detectedPois) {
        updateMapWithLatestDetectedPOIs(detectedPois);
    }

    /**
     * Updates the map when trackable POIs are detected such that only the
     * currently detected POIs are rendered on the map
     *
     * @param detectedPois
     */
    private void updateMapWithLatestDetectedPOIs(List<SKDetectedPOI> detectedPois) {

        List<Integer> detectedIdsList = new ArrayList<Integer>();
        for (SKDetectedPOI detectedPoi : detectedPois) {
            detectedIdsList.add(detectedPoi.getPoiID());
        }
        for (int detectedPoiId : detectedIdsList) {
            if (detectedPoiId == -1) {
                continue;
            }
            if (drawnTrackablePOIs.get(detectedPoiId) == null) {
                drawnTrackablePOIs.put(detectedPoiId, trackablePOIs.get(detectedPoiId));
                drawDetectedPOI(detectedPoiId);
            }
        }
        for (int drawnPoiId : new ArrayList<Integer>(drawnTrackablePOIs.keySet())) {
            if (!detectedIdsList.contains(drawnPoiId)) {
                drawnTrackablePOIs.remove(drawnPoiId);
                mapView.deleteAnnotation(drawnPoiId);
            }
        }
    }

    /**
     * Draws a detected trackable POI as an annotation on the map
     *
     * @param poiId
     */
    private void drawDetectedPOI(int poiId) {
        SKAnnotation annotation = new SKAnnotation(poiId);
        SKTrackablePOI poi = trackablePOIs.get(poiId);
        annotation.setLocation(poi.getCoordinate());
        annotation.setMininumZoomLevel(5);
        annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_PURPLE);
        mapView.addAnnotation(annotation, SKAnimationSettings.ANIMATION_NONE);

    }

    @Override
    public void onUpdatePOIsInRadius(double latitude, double longitude, int radius) {

        // set the POIs to be tracked by the POI tracker
        poiTrackingManager.setTrackedPOIs(SKTrackablePOIType.SPEEDCAM.getValue(),
                new ArrayList<SKTrackablePOI>(trackablePOIs.values()));
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
                            int currentExactScreenOrientation = Utils.getExactScreenOrientation(this);
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


    /**
     * Enables/disables heading mode
     *
     * @param enabled
     */
    private void setHeading(boolean enabled) {
        if (enabled) {
            headingOn = true;
            mapView.getMapSettings().setHeadingMode(SKHeadingMode.ROTATING_MAP);
            startOrientationSensor();
        } else {
            headingOn = false;
            mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.NONE);
            stopOrientationSensor();
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

    @Override
    public void onCurrentPositionUpdate(SKPosition currentPosition) {
        this.currentPositionTime = System.currentTimeMillis();
        this.currentPosition = currentPosition;
        SKPositionerManager.getInstance().reportNewGPSPosition(this.currentPosition);
        if (skToolsNavigationInProgress) {
            if (this.currentPosition.getHorizontalAccuracy() >= 150) {
                numberOfConsecutiveBadPositionReceivedDuringNavi++;
                if (numberOfConsecutiveBadPositionReceivedDuringNavi >= 3) {
                    numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
                    onGPSSignalLost();
                }
            } else {
                numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
                onGPSSignalRecovered();
            }
        }
    }

    /**
     * Called when the gps signal was lost
     */
    private void onGPSSignalLost() {
        navigationManager.showSearchingForGPSPanel();
    }

    /**
     * Called when the gps signal was recovered after a loss
     */
    private void onGPSSignalRecovered() {
        navigationManager.hideSearchingForGPSPanel();
    }

    @Override
    public void onOnlineRouteComputationHanging(int status) {

    }


    // map interaction callbacks ...
    @Override
    public void onActionPan() {
        if (headingOn) {
            setHeading(false);
        }
    }

    @Override
    public void onActionZoom() {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (navigationManager != null && skToolsNavigationInProgress) {
            navigationManager.notifyOrientationChanged();
        }
    }

    @Override
    public void onAnnotationSelected(final SKAnnotation annotation) {
        if (navigationUI.getVisibility() == View.VISIBLE) {
            return;
        }
        // show the popup at the proper position when selecting an
        // annotation
        int annotationHeight = 0;
        float annotationOffset = annotation.getOffset().getY();
        switch (annotation.getUniqueID()) {
            case 10:
                annotationHeight = (int) (64 * getResources().getDisplayMetrics().density);
                popupTitleView.setText("Annotation using texture ID");
                popupDescriptionView.setText(" Red pin");
                break;
            case 11:
                annotationHeight = customView.getHeight();
                popupTitleView.setText("Annotation using custom view");
                popupDescriptionView.setText(null);
                break;
        }
        mapPopup.setVerticalOffset(-annotationOffset + annotationHeight / 2);
        mapPopup.showAtLocation(annotation.getLocation(), true);
    }


    @Override
    public void onCustomPOISelected(SKMapCustomPOI customPoi) {

    }


    @Override
    public void onDoubleTap(SKScreenPoint point) {

    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onLongPress(SKScreenPoint point) {

        SKCoordinate poiCoordinates = mapView.pointToCoordinate(point);
        final SKSearchResult place = SKReverseGeocoderManager
                .getInstance().reverseGeocodePosition(poiCoordinates);

        boolean selectPoint = isStartPointBtnPressed || isEndPointBtnPressed || isViaPointSelected;
        if (poiCoordinates != null && place != null && selectPoint) {
            SKAnnotation annotation = new SKAnnotation(GREEN_PIN_ICON_ID);
            if (isStartPointBtnPressed) {
                annotation.setUniqueID(GREEN_PIN_ICON_ID);
                annotation
                        .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
                startPoint = place.getLocation();
            } else if (isEndPointBtnPressed) {
                annotation.setUniqueID(RED_PIN_ICON_ID);
                annotation
                        .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
                destinationPoint = place.getLocation();
            } else if (isViaPointSelected) {
                annotation.setUniqueID(VIA_POINT_ICON_ID);
                annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_PURPLE);
                viaPoint = new SKViaPoint(VIA_POINT_ICON_ID, place.getLocation());
                findViewById(R.id.clear_via_point_button).setVisibility(View.VISIBLE);
            }

            annotation.setLocation(place.getLocation());
            annotation.setMininumZoomLevel(5);
            mapView.addAnnotation(annotation,
                    SKAnimationSettings.ANIMATION_NONE);
        }

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
    public void onMapRegionChanged(SKCoordinateRegion mapRegion) {
    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onSingleTap(SKScreenPoint point) {
        mapPopup.setVisibility(View.GONE);
    }


    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onInternationalisationCalled(int result) {

    }

    public SKPosition getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void onDestinationReached() {
        Toast.makeText(MapActivity.this, "Destination reached", Toast.LENGTH_SHORT).show();
        // clear the map when reaching destination
        clearMap();
    }


    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String countryCode, String streetName, String referenceName, SKNavigationState.SKStreetType streetType,
                                   double currentSpeed, double speedLimit) {

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] adviceList, boolean speedExceeded) {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState navigationState) {
    }


    @Override
    public void onVisualAdviceChanged(boolean firstVisualAdviceChanged, boolean secondVisualAdviceChanged,
                                      SKNavigationState navigationState) {
    }


    @Override
    public void onRealReachCalculationCompleted(SKBoundingBox bbox) {
        // fit the reachable area on the screen when real reach calculataion
        // ends
        mapView.fitRealReachInView(bbox, false, 0);
    }


    @Override
    public void onPOIClusterSelected(SKPOICluster poiCluster) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTunnelEvent(boolean tunnelEntered) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion mapRegion) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion mapRegion) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapVersionSet(int newVersion) {
        // TODO Auto-generated method stub

    }

    private void showUpdateDialog(final int newVersion) {
        final AlertDialog alertDialog = new AlertDialog.Builder(MapActivity.this).create();
        alertDialog.setMessage("New map version available");
        alertDialog.setCancelable(true);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.update_label),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        SKVersioningManager manager = SKVersioningManager.getInstance();
                        boolean updated = manager.updateMapsVersion(newVersion);
                        if (updated) {
                            app.getAppPrefs().saveBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED, true);
                            SplashActivity.newMapVersionDetected = 0;
                            Toast.makeText(MapActivity.this,
                                    "The map has been updated to version " + newVersion, Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(MapActivity.this, "An error occurred in updating the map ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel_label),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onNewVersionDetected(final int newVersion) {
        showUpdateDialog(newVersion);
    }

    @Override
    public void onNoNewVersionDetected() {
        Toast.makeText(MapActivity.this, "No new versions were detected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVersionFileDownloadTimeout() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCurrentPositionSelected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onObjectSelected(int id) {
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (((OneBoxExtFragment)getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).ONEBOX_ACTIVATED) {

            if(getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID) != null)
                ((OneBoxExtFragment)getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).handleBackButtonPressed();
            return;
        }

        if (skToolsNavigationInProgress || skToolsRouteCalculated) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
            alert.setTitle("Really quit?");
            alert.setMessage("Do you want to exit navigation?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                    getActionBar().setHomeButtonEnabled(true);
                    if (skToolsNavigationInProgress) {
                        navigationManager.stopNavigation();
                    } else {
                        navigationManager.removeRouteCalculationScreen();
                    }
                    initializeNavigationUI(false);
                    skToolsRouteCalculated = false;
                    skToolsNavigationInProgress = false;
                }
            });
            alert.setNegativeButton("Cancel", null);
            alert.show();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
            alert.setTitle("Really quit? ");
            alert.setMessage("Do you really want to exit the app?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    if (ResourceDownloadsListActivity.mapsDAO != null) {
                        SKToolsDownloadManager downloadManager = SKToolsDownloadManager.getInstance(new SKToolsDownloadListener() {
                            @Override
                            public void onDownloadProgress(SKToolsDownloadItem currentDownloadItem) {

                            }

                            @Override
                            public void onDownloadCancelled(String currentDownloadItemCode) {

                            }

                            @Override
                            public void onDownloadPaused(SKToolsDownloadItem currentDownloadItem) {
                                MapDownloadResource mapResource = (MapDownloadResource) ResourceDownloadsListActivity
                                        .allMapResources.get(currentDownloadItem.getItemCode());
                                mapResource.setDownloadState(currentDownloadItem.getDownloadState());
                                mapResource.setNoDownloadedBytes(currentDownloadItem.getNoDownloadedBytes());
                                ResourceDownloadsListActivity.mapsDAO.updateMapResource(mapResource);
                                app.getAppPrefs().saveDownloadStepPreference(currentDownloadItem.getCurrentStepIndex());
                                finish();
                            }

                            @Override
                            public void onInternetConnectionFailed(SKToolsDownloadItem currentDownloadItem,
                                                                   boolean responseReceivedFromServer) {

                            }

                            @Override
                            public void onAllDownloadsCancelled() {

                            }

                            @Override
                            public void onNotEnoughMemoryOnCurrentStorage(SKToolsDownloadItem currentDownloadItem) {

                            }

                            @Override
                            public void onInstallStarted(SKToolsDownloadItem currentInstallingItem) {

                            }

                            @Override
                            public void onInstallFinished(SKToolsDownloadItem currentInstallingItem) {

                            }
                        });
                        if (downloadManager.isDownloadProcessRunning()) {
                            // pause downloads when exiting app if one is currently in progress
                            downloadManager.pauseDownloadThread();
                            return;
                        }
                    }
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            alert.setNegativeButton("Cancel", null);
            alert.show();

        }

    }

    @Override
    public void onRouteCalculationCompleted(final SKRouteInfo routeInfo) {

        if (currentMapOption == MapOption.ALTERNATIVE_ROUTES) {
            int routeIndex = routeIds.size();
            routeIds.add(routeInfo.getRouteID());
            double tollsCost = TollsCostCalculator.getTollsCost(routeInfo, app);
            altRoutesButtons[routeIndex].setText(Utils.formatDistance(routeInfo.getDistance()) + "\n");
//            Utils.formatTime(routeInfo.getEstimatedTime() + "\n" + tollsCost + " EUR");
              if (routeIndex == 0) {
                // select 1st alternative by default
                selectAlternativeRoute(0);
            }
        } else if (currentMapOption == MapOption.ROUTING_AND_NAVIGATION || currentMapOption == MapOption.POI_TRACKING
                || currentMapOption == MapOption.NAVI_UI) {
            // select the current route (on which navigation will run)
            SKRouteManager.getInstance().setCurrentRouteByUniqueId(routeInfo.getRouteID());
            // zoom to the current route
            SKRouteManager.getInstance().zoomToRoute(1, 1, 8, 8, 8, 8, 0);

            if (currentMapOption == MapOption.ROUTING_AND_NAVIGATION) {
                bottomButton.setText(getResources().getString(R.string.start_navigation));
            }
        }  else if (currentMapOption == MapOption.MAP_INTERACTION) {
            if (shouldCacheTheNextRoute) {
                cachedRouteId = routeInfo.getRouteID();
            }
        }

        final List<SKRouteAdvice> advices = SKRouteManager.getInstance().getAdviceListForRouteByUniqueId(routeInfo.getRouteID(), SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
        if (advices != null) {
            for (SKRouteAdvice advice : advices) {
                SKLogging.writeLog(TAG, " Route advice is " + advice.toString(), SKLogging.LOG_DEBUG);
            }
        }

        final String[] routeSummary = routeInfo.getRouteSummary();
        if (routeSummary != null) {
            for (String street : routeSummary) {
                SKLogging.writeLog(TAG, " Route Summary street = " + street, SKLogging.LOG_ERROR);
            }
        } else {
            SKLogging.writeLog(TAG, "Route summary is null ", SKLogging.LOG_ERROR);
        }
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode arg0) {
        shouldCacheTheNextRoute = false;
        Toast.makeText(MapActivity.this, getResources().getString(R.string.route_calculation_failed),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean specialSoundFile) {
        // a new navigation advice was received
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithAudioFiles " + Arrays.asList(audioFiles), Log.DEBUG);
        SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
    }

    @Override
    public void onSignalNewAdviceWithInstruction(String instruction) {
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithInstruction " + instruction, Log.DEBUG);
        textToSpeechEngine.speak(instruction, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onSpeedExceededWithInstruction(String instruction, boolean speedExceeded) {
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onViaPointReached(int index) {
    }

    @Override
    public void onNavigationStarted() {
        skToolsNavigationInProgress = true;
        numberOfConsecutiveBadPositionReceivedDuringNavi = 0;
        if (navigationUI.getVisibility() == View.VISIBLE) {
            navigationUI.setVisibility(View.GONE);
        }

        gpsPositionsDelayChecker = new Handler();
        startPositionDelayChecker();
    }

    @Override
    public void onNavigationEnded() {
        gpsPositionsDelayChecker.removeCallbacks(gpsPositionDelayCheckerRunnable);
        skToolsRouteCalculated = false;
        skToolsNavigationInProgress = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        initializeNavigationUI(false);
    }

    /**
     * runs the recursive gps signal checker
     */
    private void startPositionDelayChecker() {
        gpsPositionsDelayChecker.postDelayed(gpsPositionDelayCheckerRunnable, 5000);
    }

    /**
     * Checks if there is no new gps position and notifies if the signal was lost
     */
    private Runnable gpsPositionDelayCheckerRunnable = new Runnable() {
        @Override
        public void run() {
            if (skToolsNavigationInProgress) {
                long lastPositionDelay = System.currentTimeMillis() - currentPositionTime;
                if (lastPositionDelay >= 5000) {
                    onGPSSignalLost();
                }
                startPositionDelayChecker();
            }
        }
    };

    @Override
    public void onRouteCalculationStarted() {
        skToolsRouteCalculated = true;
    }

    @Override
    public void onRouteCalculationCompleted() {

    }

    @Override
    public void onRouteCalculationCanceled() {
        skToolsRouteCalculated = false;
        skToolsNavigationInProgress = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initializeNavigationUI(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(!((OneBoxExtFragment)getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).ONEBOX_ACTIVATED){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

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

    public void handleMenuItemClick(MapOption mapOption) {
        clearMap();
        switch (mapOption) {
            case NAVI_UI:
                currentMapOption = MapOption.NAVI_UI;
                initializeNavigationUI(false);
                findViewById(R.id.clear_via_point_button).setVisibility(View.GONE);
                break;
            case TOURIST_ATTRACTIONS_SEARCH:
                startActivity(new Intent(this, TouristAttractionsActivity.class));
                break;
             case MAP_DOWNLOADS:
                if (Utils.isInternetAvailable(this)) {
                    startActivity(new Intent(MapActivity.this, ResourceDownloadsListActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                    .show();
                }
                break;
            case SETTINGS:
                startActivity(new Intent(MapActivity.this, SettingsActivity.class));
                break;
            case MAP_DISPLAY:
                mapView.clearHeatMapsDisplay();
                currentMapOption = MapOption.MAP_DISPLAY;
                bottomButton.setVisibility(View.GONE);
                SKRouteManager.getInstance().clearCurrentRoute();
                break;
            case MAP_INTERACTION:
                currentMapOption = MapOption.MAP_INTERACTION;
                handleMapInteractionOption();
                break;
            case MAP_OVERLAYS:
                currentMapOption = MapOption.MAP_OVERLAYS;
                drawShapes();
                mapView.setZoom(14);
                mapView.animateToLocation(new SKCoordinate(37.7765, -122.4200), 10);
                break;
            case ALTERNATIVE_ROUTES:
                currentMapOption = MapOption.ALTERNATIVE_ROUTES;
                altRoutesView.setVisibility(View.VISIBLE);
                launchAlternativeRouteCalculation();
                break;
            case MAP_STYLES:
                currentMapOption = MapOption.MAP_STYLES;
                mapStylesView.setVisibility(View.VISIBLE);
                selectStyleButton();
                break;
            case MAP_CREATOR:
                currentMapOption = MapOption.MAP_DISPLAY;
                mapView.applySettingsFromFile(app.getMapCreatorFilePath());
                break;
            case REAL_REACH:
                currentMapOption = MapOption.REAL_REACH;
                mapView.animateToLocation(new SKCoordinate(52.5233, 13.4127), 10);
                realReachLayout.setVisibility(View.VISIBLE);
                break;
            case REVERSE_GEOCODING:
                startActivity(new Intent(this, ReverseGeocodingActivity.class));
                break;
            case ADDRESS_SEARCH:
                startActivity(new Intent(this, OfflineAddressSearchActivity.class));
                break;
            case NEARBY_SEARCH:
                startActivity(new Intent(this, NearbySearchActivity.class));
                break;
            case ONEBOX_SEARCH:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                initializeOneBox();
                currentMapOption = MapOption.ONEBOX_SEARCH;
                break;
            case ANNOTATIONS:
                currentMapOption = MapOption.ANNOTATIONS;
                prepareAnnotations();
                break;
            case ROUTING_AND_NAVIGATION:
                currentMapOption = MapOption.ROUTING_AND_NAVIGATION;
                bottomButton.setVisibility(View.VISIBLE);
                bottomButton.setText(getResources().getString(R.string.calculate_route));
                break;
            case POI_TRACKING:
                currentMapOption = MapOption.POI_TRACKING;
                if (trackablePOIs == null) {
                    initializeTrackablePOIs();
                }
                launchRouteCalculation(new SKCoordinate(37.761278, -122.397674), new SKCoordinate(37.738761, -122.448270));
                break;
            case HEAT_MAP:
                currentMapOption = MapOption.HEAT_MAP;
                startActivity(new Intent(this, POICategoriesListActivity.class));
                break;
            default:
                break;
        }
        if (currentMapOption != MapOption.MAP_DISPLAY) {
            positionMeButton.setVisibility(View.GONE);
            headingButton.setVisibility(View.GONE);
        }

    }


    /**
     * Initiate real reach time profile
     */
    private void showRealReach(SKRealReachSettings.SKRealReachMeasurementUnit unitType, SKRealReachSettings.SKRealReachVehicleType vehicleType, int range, SKRouteSettings.SKRouteConnectionMode skRouteConnectionMode) {

        if (mapView == null) {
            return;
        }
        mapView.clearRealReachDisplay();

        // set listener for real reach calculation events
        mapView.setRealReachListener(this);
        // get object that can be used to specify real reach calculation
        // properties
        SKRealReachSettings realReachSettings = new SKRealReachSettings();
        // set center position for real reach
        realReachSettings.setLocation(new SKCoordinate(52.5233, 13.4127));
        // set measurement unit for real reach;
        realReachSettings.setMeasurementUnit(unitType);

        if (unitType == SKRealReachSettings.SKRealReachMeasurementUnit.MILIWATT_HOURS) {
            realReachSettings.setConsumption(energyConsumption);
            realReachSettings.setRange(range * 100);
        } else if (unitType == SKRealReachSettings.SKRealReachMeasurementUnit.SECOND) {
            realReachSettings.setRange(range * 60);
        } else {
            realReachSettings.setRange(range * 1000);
        }
        if (roundTrip) {
            realReachSettings.setRoundTrip(true);
        } else {
            realReachSettings.setRoundTrip(false);
        }
        realReachSettings.setConnectionMode(skRouteConnectionMode);
        // set the transport mode
        realReachSettings.setTransportMode(vehicleType);
        // initiate real reach
        mapView.displayRealReachWithSettings(realReachSettings);
    }

    private void handleMapInteractionOption() {

        mapView.animateToLocation(new SKCoordinate(37.7765, -122.4200), 10);

        // get the annotation object
        SKAnnotation annotation1 = new SKAnnotation(10);
        // set annotation location
        SKCoordinate annotation1Coordinate = new SKCoordinate(37.7765, -122.4200);
        annotation1.setLocation(annotation1Coordinate);
        // set minimum zoom level at which the annotation should be visible
        annotation1.setMininumZoomLevel(5);
        // set the annotation's type
        annotation1.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
        // render annotation on map
        mapView.addAnnotation(annotation1, SKAnimationSettings.ANIMATION_NONE);

        SKAnnotation annotation2 = new SKAnnotation(11);
        SKCoordinate annotation2Coordinate = new SKCoordinate(37.777142, -122.412753);
        annotation2.setLocation(annotation2Coordinate);
        annotation2.setMininumZoomLevel(5);
        annotation2.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        mapView.addAnnotation(annotation2, SKAnimationSettings.ANIMATION_NONE);

        bottomButton.setVisibility(Button.VISIBLE);
        bottomButton.setText("Open new map instance");

        shouldCacheTheNextRoute = true;

        launchRouteCalculation(annotation1Coordinate, annotation2Coordinate);
    }

    /**
     * Check if there is a cached route at the moment
     *
     * @return true if we have a cached route or false otherwise
     */
    private boolean isRouteCached() {
        return cachedRouteId != null;
    }

    /**
     * Loads a route from the route cache
     */
    public void loadRouteFromCache() {
        SKRouteManager.getInstance().loadRouteFromCache(cachedRouteId);
    }

    /**
     * Cleares the route cache and the correspondent id
     */
    public void clearRouteFromCache() {
        SKRouteManager.getInstance().clearAllRoutesFromCache();
        cachedRouteId = null;
    }

    public static SKMapViewHolder getMapViewHolder() {
        return mapViewGroup;
    }

    public static RelativeLayout getNavigationUI() {
        return navigationUI;
    }

    public SKCoordinate getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(SKCoordinate destinationPoint) {
        this.destinationPoint = destinationPoint;
    }
}
