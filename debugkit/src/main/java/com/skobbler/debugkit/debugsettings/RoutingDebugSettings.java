package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.search.SKSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 02.07.2015.
 */
public class RoutingDebugSettings extends DebugSettings implements SKRouteListener, SKMapSurfaceListener {

    /**
     * Annotation id for the start point
     */
    private static final byte GREEN_PIN_ICON_ID = 0;

    /**
     * Annotation id for the distination point
     */
    private static final byte RED_PIN_ICON_ID = 1;

    /**
     * Checks for points in long press selection mode
     */
    private boolean isStartPointBtnPressed, isEndPointBtnPressed;

    /**
     * Check if start/dest points are selected from given coordinates or from long press on the map
     */
    private boolean configPoints;

    /**
     * Request advices
     */
    private boolean requestAdvicesCheck;

    /**
     * Request coordinates
     */
    private boolean requestCoordinatesCheck;

    /**
     * Request country codes
     */
    private boolean requestCountryCodesCheck;

    /**
     * Advice list
     */
    private static ArrayList<SKRouteAdvice> advicesList;

    /**
     * Coordinates list
     */
    private static List<SKCoordinate> coordinatesList;

    /**
     * Country codes list
     */
    private static ArrayList<String> countryCodesList;

    /**
     * Block road distance
     */
    private double blockRoadDistance;

    /**
     * Check if the bloack road distance is bigger then route distance
     */
    private boolean blockRoadCheck;

    /**
     * Save route id
     */
    private int saveRouteId;

    /**
     * Save route check
     */
    private boolean saveRouteCheck = true;

    /**
     * Load route id
     */
    private int loadRouteId;

    /**
     * Toll roads check
     */
    private boolean tollRoadsCheck;
    /**
     * Highways check
     */
    private boolean highwaysCheck;
    /**
     * Ferry Lines check
     */
    private boolean ferryLinesCheck;
    /**
     * Bicycle Walk check
     */
    private boolean bicycleWalkCheck;
    /**
     * Bicycle Carry check
     */
    private boolean bicycleCarryCheck;

    /**
     * The insets for zoom to route
     */
    private int topInset, leftInset, rightInset, bottomInset;
    /**
     * get a route settings object and populate it with the desired properties
     */
    private SKRouteSettings route = new SKRouteSettings();
    /**
     * The start point(long/lat) for the route.
     */
    private SKCoordinate startPoint;
    /**
     * The destination(long/lat) point for the route
     */
    private SKCoordinate destinationPoint;
    /**
     * Route connection mode
     */
    private SKRouteSettings.SKRouteConnectionMode routeConnectionMode = SKRouteSettings.SKRouteConnectionMode.ONLINE;
    /**
     * Route mode
     */
    private SKRouteSettings.SKRouteMode routeMode = SKRouteSettings.SKRouteMode.CAR_FASTEST;
    /**
     * Route corridor width
     */
    private int routeCorridorWidth = 2000;
    /**
     * Number of routes
     */
    private int numberOfRoutes = 3;
    /**
     * Latitude
     */
    private double latitudeEndPoint = 38.7765;
    /**
     * longitude
     */
    private double longitudeEndPoint = -123.4200;
    /**
     * Latitude
     */
    private double latitudeStartPoint = 37.7765;
    /**
     * longitude
     */
    private double longitudeStartPoint = -122.4200;
    /**
     * converter from meters to feet
     */
    public static final double METERSTOFEET = 3.2808399;
    /**
     * converter from meters to yards
     */
    public static final double METERSTOYARDS = 1.0936133;
    /**
     * the number of meters in a mile
     */
    public static final double METERSINMILE = 1609.34;
    /**
     * the number of meters in a km
     */
    public static final int METERSINKM = 1000;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.settings_nav_ui), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_start_lat), latitudeStartPoint));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_start_long), longitudeStartPoint));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_longtap_start), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_dest_lat), latitudeEndPoint));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_dest_long), longitudeEndPoint));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_longtap_dest), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_routesnr), numberOfRoutes));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.select_via_point), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_routemode), routeMode));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_connection_mode), routeConnectionMode));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_restriction), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_should_be_rendered), true));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_request_advices), true));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_request_extended), false));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_request_country), false));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_advanced_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_use_slopes), false));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_download_corridor), true));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_download_corridor_widht), routeCorridorWidth));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_download_corridor_wait), false));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_destination_is_point), true));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_route_info), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_route_id), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_distance), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_estimated_time), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_corridor_download), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_calculated_after_rerouting), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_toll_roades), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_highways), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_ferry_lines), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoints_on_route), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_advice_list), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_coordinates), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_country_codes), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_actions), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_calculate_route), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_top_inset), topInset));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_left_inset), leftInset));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_bottom_inset), bottomInset));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_right_inset), rightInset));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_zoom_to_route), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_block_distance), blockRoadDistance));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_block_road), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_unlock_road), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_save_route), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_load_route), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_clear_routes_from_chace), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_get_advice_list), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_get_coordinates_list), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_get_country_codes), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_clear_all), null));

        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_debug_kit_customization;
    }

    @Override
    void defineSpecificListeners() {
        final Context context = specificLayout.getContext();
        final EditText numeberRoutes = (EditText) specificLayout.findViewById(R.id.rounting_nrroutes).findViewById(R.id.property_value);
        final View shouldBeRendered = specificLayout.findViewById(R.id.rounting_should_be_rendered);
        shouldBeRendered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox shouldBeRenderedCheckBox = (CheckBox) shouldBeRendered.findViewById(R.id.property_value);
                shouldBeRenderedCheckBox.setChecked(!shouldBeRenderedCheckBox.isChecked());
                if (shouldBeRenderedCheckBox.isChecked()) {
                    route.setRouteExposed(true);
                } else {
                    route.setRouteExposed(false);
                }

            }
        });
        final View saveRouteToCache = specificLayout.findViewById(R.id.save_route_to_cache);
        saveRouteToCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfRoutes = Integer.parseInt(numeberRoutes.getText().toString());
                if (numberOfRoutes == 1) {
                    CheckBox saveRouteCheckBox = (CheckBox) saveRouteToCache.findViewById(R.id.property_value);
                    saveRouteCheckBox.setChecked(!saveRouteCheckBox.isChecked());
                    if (saveRouteCheckBox.isChecked()) {
                        saveRouteCheck = true;
                    } else {
                        saveRouteCheck = false;
                    }
                } else {
                    Toast.makeText(activity, context.getResources().getString(R.string.block_road_toast), Toast.LENGTH_LONG).show();
                }
            }
        });

        final View requestAdvices = specificLayout.findViewById(R.id.rounting_request_advices);
        requestAdvices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox requestAdvicesCheckBox = (CheckBox) requestAdvices.findViewById(R.id.property_value);
                requestAdvicesCheckBox.setChecked(!requestAdvicesCheckBox.isChecked());
                if (requestAdvicesCheckBox.isChecked()) {
                    route.setRequestAdvices(true);
                } else {
                    route.setRequestAdvices(false);
                }
            }
        });
        final View requestExtended = specificLayout.findViewById(R.id.rounting_request_extend_route);
        requestExtended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox requestExtendedCheckBox = (CheckBox) requestExtended.findViewById(R.id.property_value);
                requestExtendedCheckBox.setChecked(!requestExtendedCheckBox.isChecked());
                if (requestExtendedCheckBox.isChecked()) {
                    route.setExtendedPointsReturned(true);
                } else {
                    route.setExtendedPointsReturned(false);
                }
            }
        });
        final View requestCountry = specificLayout.findViewById(R.id.rounting_request_country_code);
        requestCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox requestCountryCheckBox = (CheckBox) requestCountry.findViewById(R.id.property_value);
                requestCountryCheckBox.setChecked(!requestCountryCheckBox.isChecked());
                if (requestCountryCheckBox.isChecked()) {
                    route.setCountryCodesReturned(true);
                } else {
                    route.setCountryCodesReturned(false);

                }
            }
        });
        final View useSlopes = specificLayout.findViewById(R.id.rounting_use_slopes);
        useSlopes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox useSlopesCheckBox = (CheckBox) useSlopes.findViewById(R.id.property_value);
                useSlopesCheckBox.setChecked(!useSlopesCheckBox.isChecked());
                if (useSlopesCheckBox.isChecked()) {
                    route.setUseRoadSlopes(true);
                } else {
                    route.setUseRoadSlopes(false);
                }

            }
        });
        final View routeCorridor = specificLayout.findViewById(R.id.rounting_download_route_corridor);
        routeCorridor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox routeCorridorCheckBox = (CheckBox) routeCorridor.findViewById(R.id.property_value);
                routeCorridorCheckBox.setChecked(!routeCorridorCheckBox.isChecked());
                if (routeCorridorCheckBox.isChecked()) {
                    route.setDownloadRouteCorridor(true);

                } else {
                    route.setDownloadRouteCorridor(false);

                }

            }
        });
        final View waitCorridor = specificLayout.findViewById(R.id.rounting_wait_corridor_download);
        waitCorridor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox waitCorridorCheckBox = (CheckBox) waitCorridor.findViewById(R.id.property_value);
                waitCorridorCheckBox.setChecked(!waitCorridorCheckBox.isChecked());
                if (waitCorridorCheckBox.isChecked()) {
                    route.setWaitForCorridorDownload(true);
                } else {
                    route.setWaitForCorridorDownload(false);
                }

            }
        });
        final View destinationPoint = specificLayout.findViewById(R.id.rounting_destination_is_point);
        destinationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox destinationPointCheckBox = (CheckBox) destinationPoint.findViewById(R.id.property_value);
                destinationPointCheckBox.setChecked(!destinationPointCheckBox.isChecked());
                if (destinationPointCheckBox.isChecked()) {
                    route.setDestinationIsPoint(true);
                } else {
                    route.setDestinationIsPoint(false);
                }
            }
        });
        specificLayout.findViewById(R.id.rounting_via_points).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingViaPoints.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_route_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingRouteMode.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_route_connection_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingRouteConnectionMode.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_route_restriction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingRestrictions.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_via_points_on_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingViaPointInfo.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_get_advise_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestAdvicesCheck) {
                    requestAdvicesCheck = false;
                } else {
                    requestAdvicesCheck = true;
                }
            }
        });
        specificLayout.findViewById(R.id.rounting_advice_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingAdviceListInfo.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_route_coordinates_from_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestCoordinatesCheck) {
                    requestCoordinatesCheck = false;
                } else {
                    requestCoordinatesCheck = true;
                }
            }
        });
        specificLayout.findViewById(R.id.rounting_coordinates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingCoordinatesList.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.rounting_request_country_code_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestCountryCodesCheck) {
                    requestCountryCodesCheck = false;
                } else {
                    requestCountryCodesCheck = true;
                }
            }
        });
        specificLayout.findViewById(R.id.rounting_country_codes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(RoutingCountryCodesList.class).open(debugBaseLayout, RoutingDebugSettings.this);
            }
        });

        final View tapStartCoordinates = specificLayout.findViewById(R.id.rounting_longpress_start);
        final View tapDestCoordinates = specificLayout.findViewById(R.id.rounting_longpress_destination);
        final View calculateRoutes = specificLayout.findViewById(R.id.rounting_calculate_route);
        final View startPointButton = specificLayout.findViewById(R.id.rounting_longpress_start);
        final View endPointButton = specificLayout.findViewById(R.id.rounting_longpress_destination);
        final View zoomToRouteButton = specificLayout.findViewById(R.id.rounting_zoom_to_route);
        final View loadRouteFromCacheButton = specificLayout.findViewById(R.id.rounting_load_route_from_cache);
        final View clearRoutesButton = specificLayout.findViewById(R.id.rounting_clear_routes);
        final View clearRoutsFromCache = specificLayout.findViewById(R.id.rounting_clear_all_routes_from_cache);
        final EditText startLatitude = (EditText) specificLayout.findViewById(R.id.rounting_start_latitude).findViewById(R.id.property_value);
        final EditText startLongitude = (EditText) specificLayout.findViewById(R.id.rounting_start_longitude).findViewById(R.id.property_value);
        final EditText destLatitude = (EditText) specificLayout.findViewById(R.id.rounting_dest_latitude).findViewById(R.id.property_value);
        final EditText destLongitude = (EditText) specificLayout.findViewById(R.id.rounting_dest_longitude).findViewById(R.id.property_value);
        final EditText routeCorridorEditText = (EditText) specificLayout.findViewById(R.id.rounting_corridor_width).findViewById(R.id.property_value);
        final EditText topInsetEditText = (EditText) specificLayout.findViewById(R.id.rounting_top_inset).findViewById(R.id.property_value);
        final EditText leftInsetEditText = (EditText) specificLayout.findViewById(R.id.rounting_left_inset).findViewById(R.id.property_value);
        final EditText bottomInsetEditText = (EditText) specificLayout.findViewById(R.id.rounting_bottom_inset).findViewById(R.id.property_value);
        final EditText rightInsetEditText = (EditText) specificLayout.findViewById(R.id.rounting_right_inset).findViewById(R.id.property_value);
        final EditText calculatedAfterReroutingInfo = (EditText) specificLayout.findViewById(R.id.rounting_after_rerouting).findViewById(R.id.property_value);
        final EditText blockRoadDistanceEdittext = (EditText) specificLayout.findViewById(R.id.rounting_block_road_distance).findViewById(R.id.property_value);
        specificLayout.findViewById(R.id.rounting_block_road).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockRoadCheck) {
                    Toast.makeText(activity,context.getResources().getText(R.string.block_road_check),Toast.LENGTH_LONG).show();
                }
                blockRoadDistance = Double.parseDouble(blockRoadDistanceEdittext.getText().toString());
                SKNavigationManager.getInstance().blockRoad(blockRoadDistance);
                calculatedAfterReroutingInfo.setText(context.getResources().getString(R.string.yes));
            }
        });
        specificLayout.findViewById(R.id.rounting_unlock_road).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKNavigationManager.getInstance().unblockAllRoads();
                calculatedAfterReroutingInfo.setText(context.getResources().getString(R.string.no));
            }
        });
        clearRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKRouteManager.getInstance().clearCurrentRoute();
                SKRouteManager.getInstance().clearRouteAlternatives();

            }
        });
        clearRoutsFromCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKRouteManager.getInstance().clearAllRoutesFromCache();
            }
        });
        loadRouteFromCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKRouteManager.getInstance().loadRouteFromCache(loadRouteId);
                SKRouteManager.getInstance().zoomMapToCurrentRoute();
            }
        });
        zoomToRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topInset = Integer.parseInt(topInsetEditText.getText().toString());
                leftInset = Integer.parseInt(leftInsetEditText.getText().toString());
                bottomInset = Integer.parseInt(bottomInsetEditText.getText().toString());
                rightInset = Integer.parseInt(rightInsetEditText.getText().toString());
                SKRouteManager.getInstance().zoomToRoute(1, 1, topInset, bottomInset, leftInset, rightInset);
                activity.getMapView().requestRender();
            }
        });
        tapStartCoordinates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                latitudeStartPoint = Double.parseDouble(startLatitude.getText().toString());
                longitudeStartPoint = Double.parseDouble(startLongitude.getText().toString());

            }
        });

        tapDestCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitudeEndPoint = Double.parseDouble(destLatitude.getText().toString());
                longitudeEndPoint = Double.parseDouble(destLongitude.getText().toString());
            }
        });
        calculateRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeCorridorWidth = Integer.parseInt(routeCorridorEditText.getText().toString());
                numberOfRoutes = Integer.parseInt(numeberRoutes.getText().toString());
                launchRouteCalculation();
            }
        });

        startPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartPointBtnPressed = true;
                isEndPointBtnPressed = false;
                configPoints = true;
            }
        });
        endPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartPointBtnPressed = false;
                isEndPointBtnPressed = true;
                configPoints = true;

            }
        });
    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof RoutingRouteMode) {
            switch (((RoutingRouteMode) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    routeMode = SKRouteSettings.SKRouteMode.CAR_SHORTEST;
                    break;
                case 1:
                    routeMode = SKRouteSettings.SKRouteMode.CAR_FASTEST;
                    break;
                case 2:
                    routeMode = SKRouteSettings.SKRouteMode.EFFICIENT;
                    break;
                case 3:
                    routeMode = SKRouteSettings.SKRouteMode.PEDESTRIAN;
                    break;
                case 4:
                    routeMode = SKRouteSettings.SKRouteMode.BICYCLE_FASTEST;
                    break;
                case 5:
                    routeMode = SKRouteSettings.SKRouteMode.BICYCLE_SHORTEST;
                    break;
                case 6:
                    routeMode = SKRouteSettings.SKRouteMode.BICYCLE_QUIETEST;
                    break;
            }

        } else if (changedChild instanceof RoutingRouteConnectionMode) {
            switch (((RoutingRouteConnectionMode) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    routeConnectionMode = SKRouteSettings.SKRouteConnectionMode.ONLINE;
                    break;
                case 1:
                    routeConnectionMode = SKRouteSettings.SKRouteConnectionMode.OFFLINE;
                    break;
                case 2:
                    routeConnectionMode = SKRouteSettings.SKRouteConnectionMode.HYBRID;
                    break;
            }
        }
    }

    @Override
    void onChildClosed(DebugSettings closedChild) {
        super.onChildClosed(closedChild);
        activity.getMapHolder().setMapSurfaceListener(this);
        if (closedChild instanceof RoutingRestrictions) {
            if (((CheckBox) ((RoutingRestrictions) closedChild).specificLayout.findViewById(R.id.restriction_mode_0).findViewById(R.id.property_value)).isChecked()) {
                tollRoadsCheck = true;
            } else {
                tollRoadsCheck = false;
            }
            if (((CheckBox) ((RoutingRestrictions) closedChild).specificLayout.findViewById(R.id.restriction_mode_1).findViewById(R.id.property_value)).isChecked()) {
                highwaysCheck = true;
            } else {
                highwaysCheck = false;
            }
            if (((CheckBox) ((RoutingRestrictions) closedChild).specificLayout.findViewById(R.id.restriction_mode_2).findViewById(R.id.property_value)).isChecked()) {
                ferryLinesCheck = true;
            } else {
                ferryLinesCheck = false;
            }
            if (((CheckBox) ((RoutingRestrictions) closedChild).specificLayout.findViewById(R.id.restriction_mode_3).findViewById(R.id.property_value)).isChecked()) {
                bicycleWalkCheck = true;
            } else {
                bicycleWalkCheck = false;
            }
            if (((CheckBox) ((RoutingRestrictions) closedChild).specificLayout.findViewById(R.id.restriction_mode_4).findViewById(R.id.property_value)).isChecked()) {
                bicycleCarryCheck = true;
            } else {
                bicycleCarryCheck = false;
            }

        }
    }

    private void launchRouteCalculation() {

        // set start and destination points
        if (configPoints) {
            route.setStartCoordinate(new SKCoordinate(startPoint.getLongitude(), startPoint.getLatitude()));
            route.setDestinationCoordinate(new SKCoordinate(destinationPoint.getLongitude(), destinationPoint.getLatitude()));

        } else {
            route.setStartCoordinate(new SKCoordinate(longitudeStartPoint, latitudeStartPoint));
            route.setDestinationCoordinate(new SKCoordinate(longitudeEndPoint, latitudeEndPoint));
        }
        //set the avoid route type
        route.setTollRoadsAvoided(tollRoadsCheck);
        route.setHighWaysAvoided(highwaysCheck);
        route.setAvoidFerries(ferryLinesCheck);
        route.setBicycleWalkAvoided(bicycleWalkCheck);
        route.setBicycleCarryAvoided(bicycleCarryCheck);

        //set the viaPoints
        if (RoutingViaPoints.getViaPointList() != null) {
            route.setViaPoints(RoutingViaPoints.getViaPointList());
        }
        //set the route connection mode
        route.setRouteConnectionMode(routeConnectionMode);
        // set the number of routes to be calculated
        route.setNoOfRoutes(numberOfRoutes);
        // set the route mode
        route.setRouteMode(routeMode);
        // set the corridor width
        route.setRouteCorridorWidthInMeters(routeCorridorWidth);
        // set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(this);
        // pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
        SKRouteManager.getInstance().zoomMapToCurrentRoute();
    }

    public static ArrayList<SKRouteAdvice> getAdviceList() {
        return advicesList;
    }

    public static List<SKCoordinate> getCoordinatesList() {
        return coordinatesList;
    }

    public static ArrayList<String> getCountryCodesList() {
        return countryCodesList;
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
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
        if (!skRouteInfo.isCorridorDownloaded()) {
            return;
        }
        Context context = specificLayout.getContext();
        advicesList = new ArrayList<>();
        coordinatesList = new ArrayList<>();
        countryCodesList = new ArrayList<>();
        String uniqueId = String.valueOf(skRouteInfo.getRouteID());
        String distance = String.valueOf(skRouteInfo.getDistance());
        String estimated = String.valueOf(skRouteInfo.getEstimatedTime());
        final EditText routeIdInfo = (EditText) specificLayout.findViewById(R.id.rounting_route_id).findViewById(R.id.property_value);
        final EditText distanceInfo = (EditText) specificLayout.findViewById(R.id.rounting_distance).findViewById(R.id.property_value);
        final EditText estimatedTimeInfo = (EditText) specificLayout.findViewById(R.id.rounting_estimated_time).findViewById(R.id.property_value);
        final EditText corridorDownloadInfo = (EditText) specificLayout.findViewById(R.id.rounting_corridor_download).findViewById(R.id.property_value);
        final EditText containsTollRoadsInfo = (EditText) specificLayout.findViewById(R.id.rounting_toll_roads).findViewById(R.id.property_value);
        final EditText containsHighwaysInfo = (EditText) specificLayout.findViewById(R.id.rounting_highways).findViewById(R.id.property_value);
        final EditText containsFerryLinesInfo = (EditText) specificLayout.findViewById(R.id.rounting_ferrylines).findViewById(R.id.property_value);
        routeIdInfo.setText(uniqueId);
        distanceInfo.setText(distance);
        estimatedTimeInfo.setText(estimated);

        if (skRouteInfo.isContainsTollRoads()) {
            containsTollRoadsInfo.setText(context.getResources().getString(R.string.yes));
        } else {
            containsTollRoadsInfo.setText(context.getResources().getString(R.string.no));
        }
        if (skRouteInfo.isContainsHighWays()) {
            containsHighwaysInfo.setText(context.getResources().getString(R.string.yes));
        } else {
            containsHighwaysInfo.setText(context.getResources().getString(R.string.no));
        }
        if (skRouteInfo.isContainsFerryLines()) {
            containsFerryLinesInfo.setText(context.getResources().getString(R.string.yes));
        } else {
            containsFerryLinesInfo.setText(context.getResources().getString(R.string.no));
        }
        if (skRouteInfo.isCorridorDownloaded()) {
            corridorDownloadInfo.setText(context.getResources().getString(R.string.yes));
        } else {
            corridorDownloadInfo.setText(context.getResources().getString(R.string.no));
        }
        if (requestAdvicesCheck) {
            advicesList.addAll(SKRouteManager.getInstance().getAdviceList(skRouteInfo.getRouteID(), SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS));
        }
        if (requestCoordinatesCheck) {
            coordinatesList = SKRouteManager.getInstance().getCoordinatesForRoute(skRouteInfo.getRouteID());
        }
        if (requestCountryCodesCheck) {
            countryCodesList.addAll(SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(skRouteInfo.getRouteID()));
        }
        saveRouteId = skRouteInfo.getRouteID();
        if (blockRoadDistance > skRouteInfo.getDistance()) {
            blockRoadCheck=true;
        }
        else {
            blockRoadCheck=false;
        }
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {
        if (saveRouteCheck) {
            SKRouteManager.getInstance().saveRouteToCache(saveRouteId);
            loadRouteId = saveRouteId;
        }
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

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
        boolean selectPoint = isStartPointBtnPressed || isEndPointBtnPressed;
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
                        .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG);
                destinationPoint = place.getLocation();
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
}
