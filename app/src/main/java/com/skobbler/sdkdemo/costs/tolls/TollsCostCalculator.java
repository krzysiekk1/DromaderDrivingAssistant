package com.skobbler.sdkdemo.costs.tolls;

import android.content.Context;
import android.database.Cursor;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchResultParent;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.costs.utils.Country;
import com.skobbler.sdkdemo.database.ResourcesDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TollsCostCalculator {

    public static double getTollsCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.00;

        // sum += getRoadCost(routeInfo);

        if (!routeInfo.isContainsHighWays()) {
            return sum;
        }

        sum += getVignettesCost(routeInfo, app);

        return sum;
    }

    private static double getVignettesCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.00;
        String country = "";

        int routeID = routeInfo.getRouteID();
        List<String> countries = SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(routeID);

        // all roads to log // TODO shorten list of positions to geocode (too much time)
        /*List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
        Map<String, String> roads = new HashMap<String, String>();
        for (SKExtendedRoutePosition position : positions) {
            SKCoordinate coordinate = new SKCoordinate(position.getCoordinate().getLongitude(), position.getCoordinate().getLatitude());
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
            //if (areTollRoadsTraversed(roads, countryCode, app)) {
                sum += getVignetteCostByCode(countryCode);
            //}
        }

        return sum;
    }

    private static boolean areTollRoadsTraversed(Map<String, String> roads, String code, Context app){
        if (getVignetteCostByCode(code) == 0.00) {
            return false;
        }
        ArrayList<String> tollRoads = getTollRoadsByCode(code, app);
        if (tollRoads.get(0).equals("all")) {
            return true;
        }
        ArrayList<String> roadsInCountry = new ArrayList<String>();
        for (Map.Entry<String, String> roadCountry : roads.entrySet()) {
            if (roadCountry.getValue().equals(code)) {
                roadsInCountry.add(roadCountry.getKey());
            }
        }
        for (String road : roadsInCountry) {
            for (String tollRoad : tollRoads) {
                if (road.equals(tollRoad)) {
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized static ArrayList<String> getTollRoadsByCode(String code, Context app){
        String[] codeArray = new String[] {code};
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        final StringBuilder query = new StringBuilder("SELECT ").append("RoadNr")
                .append(" FROM ").append("VignetteHighways")
                .append(" WHERE ").append("CountryCode").append("=?");
        Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query.toString(), codeArray);
        if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
            ArrayList<String> tollRoads = new ArrayList<String>();
            try {
                resultCursor.moveToFirst();
                while (!resultCursor.isAfterLast()) {
                    tollRoads.add(resultCursor.getString(0));
                    resultCursor.moveToNext();
                }
            } finally {
                resultCursor.close();
            }
            return tollRoads;
        } else {
            if (resultCursor != null) {
                resultCursor.close();
            }
            return null;
        }
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