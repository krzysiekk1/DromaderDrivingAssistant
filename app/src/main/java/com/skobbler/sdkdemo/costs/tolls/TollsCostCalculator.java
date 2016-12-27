package com.skobbler.sdkdemo.costs.tolls;

import android.content.Context;
import android.database.Cursor;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchResultParent;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.costs.utils.Country;
import com.skobbler.sdkdemo.costs.utils.Road;
import com.skobbler.sdkdemo.costs.utils.TollPoint;
import com.skobbler.sdkdemo.database.ResourcesDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek
 */

public class TollsCostCalculator {

    public static double getTollsCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.00;
        int routeID = routeInfo.getRouteID();
        List<String> countries = SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(routeID);
        List<Road> roads = getRoadsTraversedByRoute(routeID);

        sum += getTollRoadsCost(routeID, roads, app);

        sum += getVignettesCost(countries, roads, app);

        return sum;
    }

    private static List<Road> getRoadsTraversedByRoute (int routeID) {
        List<Road> roads = new ArrayList<Road>();
        SKCoordinate newAdviceCoordinate;
        List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();
        boolean roadsChanged;
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
        for (SKExtendedRoutePosition pos : positions) {
            coordinates.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
        }

        // first road
        SKCoordinate startCoordinate = coordinates.get(0);
        SKCoordinate lastCoordinate = startCoordinate;
        SKSearchResult startResult = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(startCoordinate);
        String lastRoadName = startResult != null ? startResult.getName() : "";
        lastRoadName = lastRoadName.replaceAll("\\s+$", "");  // remove whitespaces etc. from the end of string
        String lastCountry = "";
        if (startResult != null && startResult.getParentsList() != null) {
            for (SKSearchResultParent parent : startResult.getParentsList()) {
                lastCountry = parent.getParentName();
            }
        }
        Road lastRoad = new Road(lastRoadName, lastCountry);
        if (!lastRoadName.equals("")) {
            roads.add(lastRoad);
        }

        // iteration through advices list - filling roads list
        List<SKRouteAdvice> advices = SKRouteManager.getInstance()
                .getAdviceListForRouteByUniqueId(routeID, SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
        for (SKRouteAdvice advice : advices) {
            List<Road> roadsToAdd = new ArrayList<Road>();
            String country = "";
            newAdviceCoordinate = new SKCoordinate(advice.getAdvicePosition().getLongitude(),
                                                                advice.getAdvicePosition().getLatitude());
            SKSearchResult searchResult = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(newAdviceCoordinate);
            String newCurrentRoadName = searchResult != null ? searchResult.getName() : "";
            newCurrentRoadName = newCurrentRoadName.replaceAll("\\s+$", "");
            if (searchResult != null && searchResult.getParentsList() != null) {
                for (SKSearchResultParent parent : searchResult.getParentsList()) {
                    country = parent.getParentName();
                }
            }
            Road newCurrentRoad = new Road(newCurrentRoadName, country);
            if (!isRoadIn(newCurrentRoad, roads) && !newCurrentRoadName.equals("")) {
                roads.add(newCurrentRoad);
                int startCoordinateNr = getNearestCoordinateNr(lastCoordinate, coordinates);
                int endCoordinateNr = getNearestCoordinateNr(newAdviceCoordinate, coordinates);
                if (lastRoadName.equals(newCurrentRoadName) && lastCountry.equals(country)) {
                    roadsChanged = false;
                } else {
                    roadsChanged = true;
                }
                if (!roadsChanged) {   // road number didn't change between points
                    roadsToAdd = getRoadsTraversedBetweenPointsWithoutRoadChange(coordinates,
                                                                                startCoordinateNr, endCoordinateNr, country);
                } else {    // road number changed between points
                    roadsToAdd = getRoadsTraversedBetweenPointsWithRoadChange(coordinates, startCoordinateNr, lastRoad,
                                                                                            endCoordinateNr, newCurrentRoad);
                }
            }
            for (Road roadToAdd : roadsToAdd) {
                if (!isRoadIn(roadToAdd, roads)) {
                    roads.add(roadToAdd);
                }
            }
            String newNextRoadName = advice.getStreetName();
            newNextRoadName = newNextRoadName.replaceAll("\\s+$", "");
            Road newNextRoad = new Road(newNextRoadName, country);
            if (!isRoadIn(newNextRoad, roads) && !newNextRoadName.equals("")) {
                roads.add(newNextRoad);
            }
            lastCoordinate = newAdviceCoordinate;
            lastRoadName = newNextRoadName;
            lastCountry = country;
        }

        // all roads to log
        for (Road road : roads) {
            SKLogging.writeLog("TollsCostCalculator", road.getNr() + ", " + road.getCountryCode(), 0);
        }

        return roads;
    }

    private static List<Road> getRoadsTraversedBetweenPointsWithoutRoadChange(List<SKCoordinate> coordinates,
                                                                int startCoordinateNr, int endCoordinateNr, String country) {
        List<SKCoordinate> coordinatesToReverseGeocode;
        List<Road> roadsToAdd = new ArrayList<Road>();
        final int density = 67;

        if (endCoordinateNr - startCoordinateNr > density) {
            coordinatesToReverseGeocode = getListOfCoordinatesBetweenAdvices(coordinates, density,
                    startCoordinateNr, endCoordinateNr);
            for (SKCoordinate coordinate : coordinatesToReverseGeocode) {
                SKSearchResult result = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(coordinate);
                String newRoadName = result != null ? result.getName() : "";
                newRoadName = newRoadName.replaceAll("\\s+$", "");
                if (result != null && result.getParentsList() != null) {
                    for (SKSearchResultParent parent : result.getParentsList()) {
                        country = parent.getParentName();
                    }
                }
                Road newRoad = new Road(newRoadName, country);
                if (!isRoadIn(newRoad, roadsToAdd) && !newRoadName.equals("")) {
                    roadsToAdd.add(newRoad);
                }
            }
        }

        return roadsToAdd;
    }

    private static List<Road> getRoadsTraversedBetweenPointsWithRoadChange(List<SKCoordinate> coordinates,
                                            int startCoordinateNr, Road lastRoad, int endCoordinateNr, Road newCurrentRoad) {
        List<Road> roadsToAdd = new ArrayList<Road>();
        List<Road> roadsToAdd1 = new ArrayList<Road>();
        List<Road> roadsToAdd2 = new ArrayList<Road>();

        if (endCoordinateNr - startCoordinateNr > 1) {
            String centerCountry = "";
            int centerCoordinateNr = (endCoordinateNr + startCoordinateNr) / 2;
            SKSearchResult sr = SKReverseGeocoderManager.getInstance()
                    .reverseGeocodePosition(coordinates.get(centerCoordinateNr));
            String centerRoadName = sr != null ? sr.getName() : "";
            centerRoadName = centerRoadName.replaceAll("\\s+$", "");
            if (sr != null && sr.getParentsList() != null) {
                for (SKSearchResultParent parent : sr.getParentsList()) {
                    centerCountry = parent.getParentName();
                }
            }
            Road centerRoad = new Road(centerRoadName, centerCountry);
            if (!isRoadIn(centerRoad, roadsToAdd) && !centerRoadName.equals("")) {
                roadsToAdd.add(centerRoad);
            }
            if (centerRoadName.equals(lastRoad.getNr()) && centerCountry.equals(lastRoad.getCountryCode())) {
                roadsToAdd1 = getRoadsTraversedBetweenPointsWithoutRoadChange(coordinates, startCoordinateNr,
                                                                                            centerCoordinateNr, centerCountry);
                roadsToAdd2 = getRoadsTraversedBetweenPointsWithRoadChange(coordinates, centerCoordinateNr, centerRoad,
                                                                                            endCoordinateNr, newCurrentRoad);
            } else if (centerRoadName.equals(newCurrentRoad.getNr()) && centerCountry.equals(newCurrentRoad.getCountryCode())) {
                roadsToAdd1 = getRoadsTraversedBetweenPointsWithRoadChange(coordinates, startCoordinateNr, lastRoad,
                                                                                            centerCoordinateNr, centerRoad);
                roadsToAdd2 = getRoadsTraversedBetweenPointsWithoutRoadChange(coordinates, centerCoordinateNr,
                                                                                            endCoordinateNr, centerCountry);
            } else {
                roadsToAdd1 = getRoadsTraversedBetweenPointsWithRoadChange(coordinates, startCoordinateNr, lastRoad,
                                                                                            centerCoordinateNr, centerRoad);
                roadsToAdd2 = getRoadsTraversedBetweenPointsWithRoadChange(coordinates, centerCoordinateNr, centerRoad,
                                                                                            endCoordinateNr, newCurrentRoad);
            }
        }
        for (Road roadToAdd1 : roadsToAdd1) {
            if (!isRoadIn(roadToAdd1, roadsToAdd)) {
                roadsToAdd.add(roadToAdd1);
            }
        }
        for (Road roadToAdd2 : roadsToAdd2) {
            if (!isRoadIn(roadToAdd2, roadsToAdd)) {
                roadsToAdd.add(roadToAdd2);
            }
        }

        return roadsToAdd;
    }

    private static double getTollRoadsCost (int routeID, List<Road> roads, Context app) {
        double sum = 0.00;
        List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
        for (SKExtendedRoutePosition pos : positions) {
            coordinates.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
        }
        List<Road> tollRoadsTraversed = getTollRoadsTraversed(roads, app);
        List<TollPoint> tollPointsOnTraversedRoads = getTollPointsOnRoads(tollRoadsTraversed, app);

        for (TollPoint tollPoint : tollPointsOnTraversedRoads) {
            SKCoordinate tollCoordinate = new SKCoordinate(tollPoint.getLongitude(), tollPoint.getLatitude());
            if (isCoordinateTraversed(tollCoordinate, coordinates)) {
                sum += tollPoint.getCost();
            }
        }

        return sum;
    }

    private static double getVignettesCost (List<String> countries, List<Road> roads, Context app) {
        double sum = 0.00;

        for (String countryCode : countries) {
            if (areVignetteHighwaysTraversed(roads, countryCode, app)) {
                sum += getVignetteCostByCode(countryCode);
            }
        }

        return sum;
    }

    private static List<Road> getTollRoadsTraversed (List<Road> roadsTraversed, Context app) {
        List<Road> tollRoadsTraversed = new ArrayList<Road>();
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        for (Road road : roadsTraversed) {
            String[] roadArray = new String[] {road.getNr(), road.getCountryCode()};
            String query = "SELECT DISTINCT " + "RoadNr" + ", " + "CountryCode" + " FROM " + "Tolls" +
                                            " WHERE " + "RoadNr" + "=?" + " AND " + "CountryCode" + "=?";
            Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query, roadArray);
            if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
                try {
                    resultCursor.moveToFirst();
                    Road newTollRoad = new Road(resultCursor.getString(0), resultCursor.getString(1));
                    if (!isRoadIn(newTollRoad, tollRoadsTraversed)) {
                        tollRoadsTraversed.add(newTollRoad);
                    }
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

    private static List<TollPoint> getTollPointsOnRoads (List<Road> roads, Context app) {
        List<TollPoint> points = new ArrayList<TollPoint>();
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        for (Road road : roads) {
            String[] roadArray = new String[] {road.getNr(), road.getCountryCode()};
            String query = "SELECT " + "Id" + ", " + "Name" + ", " + "RoadNr" + ", " + "Longitude" + ", " + "Latitude" + ", " +
                    "CountryCode" + ", " + "Cost" + " FROM " + "Tolls" +
                    " WHERE " + "RoadNr" + "=?" + " AND " + "CountryCode" + "=?";
            Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query, roadArray);
            if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
                try {
                    resultCursor.moveToFirst();
                    while (!resultCursor.isAfterLast()) {
                        TollPoint newTollPoint = new TollPoint(resultCursor.getInt(0), resultCursor.getString(1),
                                resultCursor.getString(2), resultCursor.getString(3), resultCursor.getString(4),
                                resultCursor.getString(5), resultCursor.getDouble(6));
                        if (!isPointIn(newTollPoint, points)) {
                            points.add(newTollPoint);
                        }
                        resultCursor.moveToNext();
                    }
                } finally {
                    resultCursor.close();
                }
            } else {
                if (resultCursor != null) {
                    resultCursor.close();
                }
            }
        }

        return points;
    }

    private static boolean isCoordinateTraversed(SKCoordinate tollCoordinate, List<SKCoordinate> coordinates) {
        int nearestCoordinateNr = getNearestCoordinateNr(tollCoordinate, coordinates);
        double nearestDistance = SKToolsUtils.distanceBetween(tollCoordinate, coordinates.get(nearestCoordinateNr));
        double distanceBefore = 10000;
        double distanceNext = 10000;
        double nearestDistance2;
        int nearestCoordinateNr2 = -1;
        final int newPointsNr = 7;  // new coordinates to check
        List<SKCoordinate> newCoordinatesToCheck = new ArrayList<SKCoordinate>();

        if (nearestDistance > 150) {
            return false;
        }
        if (nearestDistance <= 100) {
            return true;
        }
        if (nearestCoordinateNr > 0) {
            distanceBefore = SKToolsUtils.distanceBetween(tollCoordinate, coordinates.get(nearestCoordinateNr - 1));
        }
        if (nearestCoordinateNr < coordinates.size() - 1) {
            distanceNext = SKToolsUtils.distanceBetween(tollCoordinate, coordinates.get(nearestCoordinateNr + 1));
        }
        nearestDistance2 = Math.min(distanceBefore, distanceNext);
        if (nearestDistance2 < 10000) {
            if (nearestDistance2 == distanceBefore) {
                nearestCoordinateNr2 = nearestCoordinateNr - 1;
            } else {
                nearestCoordinateNr2 = nearestCoordinateNr + 1;
            }
            if (nearestCoordinateNr2 >= 0) {
                for (int pointNr = 1; pointNr <= newPointsNr; pointNr++) {
                    SKCoordinate newCoordinate = getNewCoordinateToCheck(coordinates, nearestCoordinateNr, nearestCoordinateNr2,
                            pointNr, newPointsNr);
                    newCoordinatesToCheck.add(newCoordinate);
                }
            }
            for (SKCoordinate coordinateToCheck : newCoordinatesToCheck) {
                if (SKToolsUtils.distanceBetween(tollCoordinate, coordinateToCheck) <= 100) {
                    return true;
                }
            }
        }
        return false;
    }

    private static SKCoordinate getNewCoordinateToCheck (List<SKCoordinate> coordinates, int coord1Nr, int coord2Nr,
                                                         int pointNr, int newPointsNr) {
        double longDistance = coordinates.get(coord2Nr).getLongitude() - coordinates.get(coord1Nr).getLongitude();
        double latDistance = coordinates.get(coord2Nr).getLatitude() - coordinates.get(coord1Nr).getLatitude();
        double newLongitude = coordinates.get(coord1Nr).getLongitude() + ((longDistance / (newPointsNr + 1)) * pointNr);
        double newLatitude = coordinates.get(coord1Nr).getLatitude() + ((latDistance / (newPointsNr + 1)) * pointNr);
        return new SKCoordinate(newLongitude, newLatitude);
    }

    private static boolean areVignetteHighwaysTraversed (List<Road> roads, String code, Context app) {
        if (getVignetteCostByCode(code) == 0.00) {
            return false;
        }
        ArrayList<String> vignetteHighways = getVignetteHighwaysByCode(code, app);
        if (vignetteHighways != null) {
            if (vignetteHighways.get(0).equals("all")) {
                return true;
            }
        }
        ArrayList<String> roadsInCountry = new ArrayList<String>();
        for (Road roadCountry : roads) {
            if (roadCountry.getCountryCode().equals(code)) {
                roadsInCountry.add(roadCountry.getNr());
            }
        }
        for (String road : roadsInCountry) {
            if (vignetteHighways != null) {
                for (String highway : vignetteHighways) {
                    if (road.equals(highway)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private synchronized static ArrayList<String> getVignetteHighwaysByCode (String code, Context app) {
        String[] codeArray = new String[] {code};
        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();

        String query = "SELECT " + "RoadNr" + " FROM " + "VignetteHighways" + " WHERE " + "CountryCode" + "=?";
        Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query, codeArray);
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

    private static int getNearestCoordinateNr (SKCoordinate adviceCoordinate, List<SKCoordinate> coordinatesList) {
        int nearestCoordinateNr = 0;
        double nearestDistance = 10000;
        int counter = 0;

        for (SKCoordinate coordinate : coordinatesList) {
            double distance = SKToolsUtils.distanceBetween(coordinate, adviceCoordinate);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestCoordinateNr = counter;
            }
            counter ++;
        }

        return nearestCoordinateNr;
    }

    private static List<SKCoordinate> getListOfCoordinatesBetweenAdvices (List<SKCoordinate> coordinatesList, int density,
                                                                          int startCoordinateNr, int endCoordinateNr) {
        List<SKCoordinate> finalList = new ArrayList<SKCoordinate>();

        for (int coordinateNr = startCoordinateNr+1; coordinateNr < endCoordinateNr; coordinateNr += density) {
            finalList.add(coordinatesList.get(coordinateNr));
        }

        return finalList;
    }

    private static boolean isRoadIn (Road road, List<Road> list) {
        for (Road roadFromList : list) {
            if (roadFromList.getNr().equals(road.getNr()) && roadFromList.getCountryCode().equals(road.getCountryCode())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPointIn (TollPoint point, List<TollPoint> list) {
        for (TollPoint pointFromList : list) {
            if (pointFromList.getId() == point.getId()) {
                return true;
            }
        }
        return false;
    }

}
