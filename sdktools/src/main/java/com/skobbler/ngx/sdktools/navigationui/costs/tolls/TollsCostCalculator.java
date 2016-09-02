package com.skobbler.ngx.sdktools.navigationui.costs.tolls;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.sdktools.navigationui.costs.utils.Country;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchResultParent;
import com.skobbler.ngx.util.SKLogging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TollsCostCalculator {

    public static double getTollsCost (SKRouteInfo routeInfo) {
        double sum = 0.00;

        // sum += getRoadCost(routeInfo);

        if (!routeInfo.isContainsHighWays()) {
            return sum;
        }

        sum += getVignettesCost(routeInfo);

        return sum;
    }

    private static double getVignettesCost (SKRouteInfo routeInfo) {
        double sum = 0.00;
        String country = "";

        int routeID = routeInfo.getRouteID();
        List<String> countries = SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(routeID);

        // all roads to log // TODO shorten list of positions to geocode (too much time)
        /*List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRoute(routeID);
        Map<String, String> roads = new HashMap<String, String>();
        for (SKExtendedRoutePosition position : positions) {
            SKCoordinate coordinate = position.getCoordinate();
            SKSearchResult result = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(coordinate);
            String newRoad = result != null ? result.getName() : "NULL";
            if (result != null && result.getParentsList() != null) {
                for (SKSearchResultParent parent : result.getParentsList()) {
                    country = parent.getParentName();
                }
            }
            if (!roads.containsKey(newRoad) && !newRoad.equals("NULL")) {
                roads.put(newRoad, country);
            }
        }
        for (Map.Entry<String, String> road : roads.entrySet()) {
            SKLogging.writeLog("TollsCostCalculator", road.getKey() + ", " + road.getValue(), 0);
        }*/

        for (String countryCode : countries) {
            sum += getVignetteCostByCode(countryCode);
        }

        return sum;
    }

    private static double getVignetteCostByCode(String code){
        for (Country e : Country.values()){
            if (code.equals(e.getCode())) {
                return e.getVignetteCost();
            }
        }
        return 0.00;
    }

}
