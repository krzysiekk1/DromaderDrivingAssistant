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
        int routeID = routeInfo.getRouteID();
        List<String> countries = SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(routeID);
        //Map<String, String> roads = getRoadsTraversedByRoute(routeID);

        // sum += getRoadCost(routeID, countries, roads, app);

        //sum += getVignettesCost(countries, roads, app);

        return sum;
    }

    private static Map<String, String> getRoadsTraversedByRoute (int routeID) {
        String country = "";

        // all roads to log // TODO shorten list of positions to geocode (too much time)
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
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
        }

        return roads;
    }

    private static double getVignettesCost (List<String> countries, Map<String, String> roads, Context app) {
        double sum = 0.00;

        for (String countryCode : countries) {
            //if (areVignetteHighwaysTraversed(roads, countryCode, app)) {
            sum += getVignetteCostByCode(countryCode);
            //}
        }

        return sum;
    }

    private static Map<String, String> getTollRoadsTraversed (Map<String, String> roadsTraversed, Context app) {
        Map<String, String> tollRoadsTraversed = new HashMap<String, String>();
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        for (Map.Entry<String, String> road : roadsTraversed.entrySet()) {
            String[] roadArray = new String[] {road.getKey(), road.getValue()};
            final StringBuilder query = new StringBuilder("SELECT DISTINCT ").append("RoadNr").append(", ").append("CountryCode")
                    .append(" FROM ").append("Tolls")
                    .append(" WHERE ").append("RoadNr").append("=?")
                    .append(" AND ").append("CountryCode").append("=?");
            Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query.toString(), roadArray);
            if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
                try {
                    resultCursor.moveToFirst();
                    tollRoadsTraversed.put(resultCursor.getString(0), resultCursor.getString(1));
                } finally {
                    resultCursor.close();
                }
            } else {
                if (resultCursor != null) {
                    resultCursor.close();
                }
            }
        }

        return tollRoadsTraversed;
    }

    private static boolean areVignetteHighwaysTraversed (Map<String, String> roads, String code, Context app) {
        if (getVignetteCostByCode(code) == 0.00) {
            return false;
        }
        ArrayList<String> vignetteHighways = getVignetteHighwaysByCode(code, app);
        if (vignetteHighways.get(0).equals("all")) {
            return true;
        }
        ArrayList<String> roadsInCountry = new ArrayList<String>();
        for (Map.Entry<String, String> roadCountry : roads.entrySet()) {
            if (roadCountry.getValue().equals(code)) {
                roadsInCountry.add(roadCountry.getKey());
            }
        }
        for (String road : roadsInCountry) {
            for (String highway : vignetteHighways) {
                if (road.equals(highway)) {
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized static ArrayList<String> getVignetteHighwaysByCode (String code, Context app) {
        String[] codeArray = new String[] {code};
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        final StringBuilder query = new StringBuilder("SELECT ").append("RoadNr")
                .append(" FROM ").append("VignetteHighways")
                .append(" WHERE ").append("CountryCode").append("=?");
        Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query.toString(), codeArray);
        if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
            ArrayList<String> vignetteHighways = new ArrayList<String>();
            try {
                resultCursor.moveToFirst();
                while (!resultCursor.isAfterLast()) {
                    vignetteHighways.add(resultCursor.getString(0));
                    resultCursor.moveToNext();
                }
            } finally {
                resultCursor.close();
            }
            return vignetteHighways;
        } else {
            if (resultCursor != null) {
                resultCursor.close();
            }
            return null;
        }
    }

    private static double getVignetteCostByCode (String code) {
        for (Country e : Country.values()){
            if (code.equals(e.getCode())) {
                return e.getVignetteCost();
            }
        }
        return 0.00;
    }

}