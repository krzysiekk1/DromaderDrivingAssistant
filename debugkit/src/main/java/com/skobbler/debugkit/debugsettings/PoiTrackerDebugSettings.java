package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.activity.DebugMapActivity;
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
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.poitracker.SKDetectedPOI;
import com.skobbler.ngx.poitracker.SKPOITrackerListener;
import com.skobbler.ngx.poitracker.SKPOITrackerManager;
import com.skobbler.ngx.poitracker.SKTrackablePOI;
import com.skobbler.ngx.poitracker.SKTrackablePOIRule;
import com.skobbler.ngx.poitracker.SKTrackablePOIType;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.sdktools.navigationui.SKToolsAdvicePlayer;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.util.SKLogging;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AlexandraP on 08.07.2015.
 */
public class PoiTrackerDebugSettings extends DebugSettings implements SKMapSurfaceListener, SKRouteListener, SKNavigationListener, SKPOITrackerListener {


    private static final byte GREEN_PIN_ICON_ID = 0;

    private static final byte DESTINATION_PIN_ICON_ID = 1;

    public int TRACKABLE_POI_POINT_ICON_ID = 2;

    private static final String TAG = "POITrackerDebugSettings";

    /*
    The radius of the POI coverage area
     */
    private int radiusInMeters = 9000;
    /*
    The percentage of outer coverage area which, when reached, triggers a refresh of the whole coverage area. It has to be in
    (0.0, 0.5), default is 0.1.
     */
    private double refreshMargin = 0.1;
    /**
     * The maximum distance of the shortest route to the SKTrackablePOI , in
     * order to be detected.
     */
    private int routeDistance = 1500;

    /**
     * The maximum aerial distance to the SKTrackablePOI, in order to be
     * detected.
     */
    private int aerialDistance = 3000;

    /**
     * The maximum number of turns on the shortest route to the SKTrackablePOI ,
     * in order to be detected.
     */
    private int numberOfTurns = 2;

    /**
     * The GPS accuracy threshold above which the SKTrackablePOI will be
     * ignored, in meters. Default is 100 meters.
     */
    private int maxGPSAccuracy = 100;

    /**
     * The speed threshold above which the straight distance to the
     * SKTrackablePOI , after the last turn on the route to it , is ignored.
     * Default is 80 km/h.
     */
    private double minSpeedIgnoreDistanceAfterTurn =  80;

    /**
     * The distance threshold that eliminates POIs that are far away from the
     * last corner to them. Default is 300 meters.
     */
    private int maxDistanceAfterTurn = 300;

    /**
     * If set to YES, the tracker will eliminate SKTrackablePOIs that are placed
     * after an U-turn. Default is true.
     */
    private boolean eliminateIfUTurn = true;

    /**
     * If set to TRUE, an audio warning will be played, via
     * SKNavigationDelegate. Default is FALSE.
     */
    private boolean playAudioWarning = false;

    private boolean isStartLocationPressed = false, isDestLocationPressed = false, isPOILocationPressed = false;

    private Context context;

    /**
     * The start point(long/lat) for the route.
     */
    private SKCoordinate startPoint;

    /**
     * The destination(long/lat) point for the route
     */
    private SKCoordinate destinationPoint;

    /*
    Current map view
     */
    private SKMapSurfaceView currentMapView;

    /**
     * POIs to be detected on route
     */
    private Map<Integer, SKTrackablePOI> trackablePOIs;

    /**
     * Tracker manager object
     */
    private SKPOITrackerManager poiTrackingManager;

    private static List<SKDetectedPOI> detectedPOIList = new ArrayList<SKDetectedPOI>();

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {

        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        context = specificLayout.getContext();

        trackablePOIs = new HashMap<Integer, SKTrackablePOI>();
        poiTrackingManager = new SKPOITrackerManager(PoiTrackerDebugSettings.this);

        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.tracker_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.tracker_radius), radiusInMeters));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.refresh_margin), refreshMargin));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.rule_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.route_distance), routeDistance));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.aerial_distance), aerialDistance));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.number_turns), numberOfTurns));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.start_location), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.dest_location), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.poi_location), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.advanced_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.gps_accuracy), maxGPSAccuracy));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.min_speed_ignore), minSpeedIgnoreDistanceAfterTurn));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.max_distance_after_turn), maxDistanceAfterTurn));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.eliminate_uturn), eliminateIfUTurn));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.play_audio_warnings), playAudioWarning));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.actions), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.start_poi_tracker), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.stop_poi_tracker), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.detected_pois), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.detected_pois_list), null));

        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.poi_tracker_debug_settings;
    }

    @Override
    void defineSpecificListeners() {
        SeekBar seekBarTrackerRadius = (SeekBar) specificLayout.findViewById(R.id.tracker_radius).findViewById(R.id.property_seekbar);
        seekBarTrackerRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.tracker_radius).findViewById(R.id.property_value)).setText(value + "");
                radiusInMeters = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarRefreshMargin = (SeekBar) specificLayout.findViewById(R.id.refresh_margin).findViewById(R.id.property_seekbar);
        seekBarRefreshMargin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                double progress = ((double) value / 10.0);
                ((TextView) specificLayout.findViewById(R.id.refresh_margin).findViewById(R.id.property_value)).setText(progress + "");
                refreshMargin = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarRouteDistance = (SeekBar) specificLayout.findViewById(R.id.route_distance).findViewById(R.id.property_seekbar);
        seekBarRouteDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.route_distance).findViewById(R.id.property_value)).setText(value + "");
                routeDistance = value;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarAerialDistance = (SeekBar) specificLayout.findViewById(R.id.aerial_distance).findViewById(R.id.property_seekbar);
        seekBarAerialDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.aerial_distance).findViewById(R.id.property_value)).setText(value + "");
                aerialDistance = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarGPSAccuracy = (SeekBar) specificLayout.findViewById(R.id.max_gps_accuracy).findViewById(R.id.property_seekbar);
        seekBarGPSAccuracy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.max_gps_accuracy).findViewById(R.id.property_value)).setText(value + "");
                maxGPSAccuracy = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarMinSpeed = (SeekBar) specificLayout.findViewById(R.id.min_speed_ignore_dist).findViewById(R.id.property_seekbar);
        seekBarMinSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                double progress = ((double) value / 1.0);
                ((TextView) specificLayout.findViewById(R.id.min_speed_ignore_dist).findViewById(R.id.property_value)).setText(progress + "");
                minSpeedIgnoreDistanceAfterTurn = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarMaxDistance = (SeekBar) specificLayout.findViewById(R.id.max_distance_after_turn).findViewById(R.id.property_seekbar);
        seekBarMaxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.max_distance_after_turn).findViewById(R.id.property_value)).setText(value + "");
                maxDistanceAfterTurn = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        specificLayout.findViewById(R.id.eliminate_uturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) specificLayout.findViewById(R.id.eliminate_uturn).findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                eliminateIfUTurn = checkBox.isChecked();
            }
        });
        specificLayout.findViewById(R.id.play_audio_warning).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) specificLayout.findViewById(R.id.play_audio_warning).findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                playAudioWarning = checkBox.isChecked();
            }
        });
        specificLayout.findViewById(R.id.start_location).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 isStartLocationPressed = true;
                 isDestLocationPressed = false;
                 isPOILocationPressed = false;
                 Toast.makeText(context, context.getResources().getString(R.string.long_tap_start_location),
                         Toast.LENGTH_LONG).show();
             }
         });
        specificLayout.findViewById(R.id.destination_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDestLocationPressed = true;
                isStartLocationPressed = false;
                isPOILocationPressed = false;
                Toast.makeText(context, context.getResources().getString(R.string.long_tap_destination_location),
                        Toast.LENGTH_LONG).show();
            }
        });

        specificLayout.findViewById(R.id.poi_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDestLocationPressed = false;
                isStartLocationPressed = false;
                isPOILocationPressed = true;
                Toast.makeText(context, context.getResources().getString(R.string.long_tap_poi_location),
                        Toast.LENGTH_LONG).show();
            }
        });
        specificLayout.findViewById(R.id.start_poi_tracker).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (trackablePOIs == null) {
                     Toast.makeText(context, "Press Add POI Location to select point from map",
                             Toast.LENGTH_LONG).show();
                     return;
                 }
                 if (startPoint == null || destinationPoint == null) {
                     Toast.makeText(context, "Select start point, destination point ",
                             Toast.LENGTH_LONG).show();
                     return;
                 }
                 launchRouteCalculation(startPoint, destinationPoint);
             }
         });
        specificLayout.findViewById(R.id.stop_poi_tracker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poiTrackingManager.stopPOITracker();
                SKNavigationManager.getInstance().stopNavigation();
                SKRouteManager.getInstance().clearCurrentRoute();
                // remove the detected POIs from the map
                activity.getMapView().deleteAllAnnotationsAndCustomPOIs();
            }
        });
        specificLayout.findViewById(R.id.detected_pois_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(DetectedPOIDebugSettings.class).open(debugBaseLayout, PoiTrackerDebugSettings.this);
            }
        });

    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.tracker_radius).findViewById(R.id.property_seekbar)).setMax(9000);
        ((SeekBar) specificLayout.findViewById(R.id.refresh_margin).findViewById(R.id.property_seekbar)).setMax(5);
        ((SeekBar) specificLayout.findViewById(R.id.route_distance).findViewById(R.id.property_seekbar)).setMax(3000);
        ((SeekBar) specificLayout.findViewById(R.id.aerial_distance).findViewById(R.id.property_seekbar)).setMax(3000);
        ((SeekBar) specificLayout.findViewById(R.id.max_gps_accuracy).findViewById(R.id.property_seekbar)).setMax(400);
        ((SeekBar) specificLayout.findViewById(R.id.min_speed_ignore_dist).findViewById(R.id.property_seekbar)).setMax(400);
        ((SeekBar) specificLayout.findViewById(R.id.max_distance_after_turn).findViewById(R.id.property_seekbar)).setMax(400);
    }

    @Override
    void onOpened() {
        super.onOpened();
        activity.getMapHolder().setMapSurfaceListener(this);
    }

    @Override
    void onClose() {
        super.onClose();
        activity.getMapHolder().setMapSurfaceListener(activity);

    }

    @Override
    void onChildClosed(DebugSettings closedChild) {
        super.onChildClosed(closedChild);
        activity.getMapHolder().setMapSurfaceListener(PoiTrackerDebugSettings.this);
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
        SKRouteManager.getInstance().setRouteListener(PoiTrackerDebugSettings.this);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
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


        boolean selectPoint = isStartLocationPressed || isDestLocationPressed || isPOILocationPressed;
        if (poiCoordinates != null && place != null && selectPoint) {
            SKAnnotation annotation = new SKAnnotation(GREEN_PIN_ICON_ID);
            if (isStartLocationPressed) {
                annotation.setUniqueID(GREEN_PIN_ICON_ID);
                annotation
                        .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
                startPoint = place.getLocation();
            } else if (isDestLocationPressed) {
                annotation.setUniqueID(DESTINATION_PIN_ICON_ID);
                annotation
                        .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG);
                destinationPoint = place.getLocation();
            } else if (isPOILocationPressed) {
                annotation.setUniqueID(TRACKABLE_POI_POINT_ICON_ID++);
                annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_MARKER);
                SKTrackablePOI skTrackablePOI = new SKTrackablePOI(TRACKABLE_POI_POINT_ICON_ID, 0, place.getLocation(), -1, place.getName());
                trackablePOIs.put(TRACKABLE_POI_POINT_ICON_ID, skTrackablePOI);
            }

            annotation.setLocation(place.getLocation());
            annotation.setMininumZoomLevel(5);
            activity.getMapView().addAnnotation(annotation,
                    SKAnimationSettings.ANIMATION_NONE);
        }

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

    @Override
    public void onDestinationReached() {
        Toast.makeText(context, "Destination reached", Toast.LENGTH_SHORT).show();
        // stop the POI tracker
        poiTrackingManager.stopPOITracker();
        SKNavigationManager.getInstance().stopNavigation();
        SKRouteManager.getInstance().clearCurrentRoute();
        // remove the detected POIs from the map
        activity.getMapView().deleteAllAnnotationsAndCustomPOIs();
    }

    @Override
    public void onSignalNewAdviceWithInstruction(String s) {
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithInstruction " + s, Log.DEBUG);
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] strings, boolean b) {
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithAudioFiles " + Arrays.asList(strings), Log.DEBUG);
        if(playAudioWarning){
            SKToolsAdvicePlayer.getInstance().playAdvice(strings, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
        }
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
        SKNavigationManager.getInstance().stopNavigation();
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
    public void onUpdatePOIsInRadius(double v, double v1, int i) {
        // set the POIs to be tracked by the POI tracker
        poiTrackingManager.setTrackedPOIs(SKTrackablePOIType.SPEEDCAM,
                new ArrayList<SKTrackablePOI>(trackablePOIs.values()));
    }

    @Override
    public void onReceivedPOIs(SKTrackablePOIType skTrackablePOIType, List<SKDetectedPOI> list) {
        detectedPOIList = list;
    }

    /**
     * Returns the listOfDetectedPOIs of detected POIs
     * @return
     */

    public static List<SKDetectedPOI> getListOfDetectedPOIs()
    {
        return detectedPOIList;
    }

    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
        // select the current route (on which navigation will run)
        SKRouteManager.getInstance().setCurrentRouteByUniqueId(skRouteInfo.getRouteID());
        // zoom to the current route
        SKRouteManager.getInstance().zoomToRoute(1, 1, 8, 8, 8, 8);
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {
        //set the rule on detecting POIs
        SKTrackablePOIRule skTrackablePOIRule = new SKTrackablePOIRule(routeDistance,aerialDistance,numberOfTurns,maxGPSAccuracy,minSpeedIgnoreDistanceAfterTurn,
                maxDistanceAfterTurn,eliminateIfUTurn,playAudioWarning);
        poiTrackingManager.setRuleForPOIType(SKTrackablePOIType.SPEEDCAM, skTrackablePOIRule);
        // start the POI tracker
        poiTrackingManager.startPOITrackerWithRadius(radiusInMeters,refreshMargin);
        // set warning rules for trackable POIs
        poiTrackingManager.addWarningRulesforPoiType(SKTrackablePOIType.SPEEDCAM);
        // launch navigation
        launchNavigation();

    }

    /**
     * Launches a navigation on the current route
     */
    private void launchNavigation() {
        // get navigation settings object
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        // set the desired navigation settings
        navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.SIMULATION);
        navigationSettings.setPositionerVerticalAlignment(-0.25f);
        navigationSettings.setShowRealGPSPositions(false);
        // get the navigation manager object
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(activity.getMapView());
        // set listener for navigation events
        navigationManager.setNavigationListener(PoiTrackerDebugSettings.this);

        // start navigating using the settings
        navigationManager.startNavigation(navigationSettings);
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }
}
