package com.skobbler.sdkdemo.petrolstations;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKNearbySearchSettings;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchResultParent;
import com.skobbler.ngx.search.SKSearchStatus;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.util.PreferenceTypes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by marcinsendera on 02.12.2016.
 */

public class FuelAlgorithm implements SKSearchListener{

    private static final short RADIUS = 2000;
    private static final int[] SEARCH_CATEGORY = new int[] {SKCategories.SKPOICategory.SKPOI_CATEGORY_FUEL.getValue()};

    /*
    density of taking the coordinates across route 67 it's about coordinate every ~ 2km
     */
    private static final int DENSITY = 67;
    private static final int START_COORDINATE_NR = 0;

    private List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();

    private List<GasStation> list = new ArrayList<GasStation>();

    private volatile FuelStationList stationList = new FuelStationList();

    private SKCoordinate startCoordinate;
    private SKRouteInfo routeInfo;

    private double factor;

    private Context app;

    private int maxStopsNumber;

    private double straightDistance;
    private double scaleDistance;
    private double allDistance;

    private int searchNumber;
    private int maxSearchNumber;

    private List<SKCoordinate> tempList;

    private boolean searchEnded = false;

    private FuelAlgorithmResult fuelResult;

    public FuelAlgorithm(SKRouteInfo routeInfo, Context app) {

        this.app = app;
        this.routeInfo = routeInfo;

        allDistance = routeInfo.getDistance() / 1000.0;

        this.maxStopsNumber = (int) (allDistance / 100.0);
        if (this.maxStopsNumber == 0 || this.maxStopsNumber == 1) {
            this.maxStopsNumber = 2;
        }

        if(((allDistance / 100.0) - ((double) this.maxStopsNumber)) >= 0.0){
            this.maxSearchNumber++;
        }

        int routeID = routeInfo.getRouteID();

        this.stationList = new FuelStationList();

        //getting coordinates across whole route
        this.tempList = new ArrayList<SKCoordinate>();
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);

        for (SKExtendedRoutePosition pos : positions) {
            tempList.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
        }

        Log.d("myRouteID","this is fuelalgo for: "+routeID+" positions size: "+positions.size()
                            +" last coord: "+positions.get(positions.size() - 1).getCoordinate());

        straightDistance = SKToolsUtils.distanceBetween(positions.get(0).getCoordinate(),
                                positions.get(positions.size() - 1).getCoordinate()) / 1000.0;
        startCoordinate = positions.get(0).getCoordinate();

        factor = straightDistance / allDistance;
        Log.d("factor", "factor: " + factor);
        Log.d("factor", "straightDistance: " + straightDistance);
        Log.d("factor", "allDistance: " + allDistance);

        int END_COORDINATE_NR = tempList.size() - 1;

        for (int coordinateNr = START_COORDINATE_NR + 1; coordinateNr < END_COORDINATE_NR; coordinateNr += DENSITY) {
            coordinates.add(tempList.get(coordinateNr));
        }

        this.searchNumber = START_COORDINATE_NR + 1;
        this.maxSearchNumber = END_COORDINATE_NR;

    }

    private void startSearch(int nr){
            SKSearchManager searchManager = new SKSearchManager(this);
            SKNearbySearchSettings searchObject = new SKNearbySearchSettings();
            searchObject.setRadius(this.RADIUS);
            searchObject.setSearchCategories(this.SEARCH_CATEGORY);
            searchObject.setSearchResultsNumber(100);
            searchObject.setSearchTerm("");
            searchObject.setSearchMode(SKSearchManager.SKSearchMode.OFFLINE);
            searchObject.setLocation(tempList.get(nr));
            SKSearchStatus status = searchManager.nearbySearch(searchObject);
            searchNumber += DENSITY;
            if (status != SKSearchStatus.SK_SEARCH_NO_ERROR) {
                SKLogging.writeLog("SKSearchStatus: ", status.toString(), 0);
            }
    }

    @Override
    public void onReceivedSearchResults(final List<SKSearchResult> searchResults){
        for (SKSearchResult result : searchResults) {
            stationList.addToList(result.getLocation());
        }
        Log.d("searchResults size: ", String.valueOf(searchResults.size()));
        Log.d("stationList size: ", String.valueOf(stationList.list.size()));
        Log.d("results routeID: ",String.valueOf(routeInfo.getRouteID()));
        if (searchNumber < maxSearchNumber) {
            startSearch(searchNumber);
        } else {
            Log.d("SEARCH_ENDED ",String.valueOf(routeInfo.getRouteID()));
            setSearchEnded(true);
        }
    }

    private void setSearchEnded(boolean ended) {
        this.searchEnded = ended;
    }

    public FuelAlgorithmResult getMinimalCost() {

        // run first search
        startSearch(searchNumber);

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<FuelAlgorithmResult> result = es.submit(new Callable<FuelAlgorithmResult>() {
            public FuelAlgorithmResult call() throws Exception {
                while (!searchEnded) {
                    try {
                        Log.d("FUEL_ALGORITHM ", String.valueOf(routeInfo.getRouteID()));
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
                String petrolType = sharedPreferences.getString(PreferenceTypes.K_FUEL_TYPE, "0");

                Log.d("petrolType", "petrol type is set to: "+petrolType);
                Log.d("FuelStationStructure","size: "+ stationList.list.size());

                for(FuelStationStructure station: stationList.list){

                    Log.d("for statement", "for statement");
                    String countryCode = null;

                    SKSearchResult searchResult = SKReverseGeocoderManager.getInstance()
                                                    .reverseGeocodePosition(station.getCoordinates());
                    if (searchResult != null && searchResult.getParentsList() != null) {
                        for (SKSearchResultParent parent : searchResult.getParentsList()) {
                            countryCode = parent.getParentName();
                        }
                    }

                    FuelCostAtStation fuelCostAtStation = new FuelCostAtStation();

                    fuelCostAtStation.calculateFuelCostAtStation(station.getCoordinates(), countryCode, app);
                    double diesel = fuelCostAtStation.getDieselLiterCost();
                    double petrol = fuelCostAtStation.getPetrolLiterCost();
                    double lpg = fuelCostAtStation.getLPGLiterCost();

                    double distance = ((SKToolsUtils.distanceBetween(startCoordinate.getLatitude(),
                                        startCoordinate.getLongitude(), station.getCoordinates().getLongitude(),
                                                            station.getCoordinates().getLatitude()))/1000.0) * factor;
                    Log.d("station distance", "startCoord: "+startCoordinate.toString()
                                            +" stationCoord: " +station.getCoordinates().toString());
                    Log.d("longlati","startLong: "+ startCoordinate.getLongitude()+" startLati: "+startCoordinate.getLatitude()
                                                                    +" stationLong: "+station.getCoordinates().getLongitude()
                                                                    +" stationLati: "+station.getCoordinates().getLatitude());
                    Log.d("station distance", "distance "+distance);
                    station.setDieselCost(diesel);
                    station.setPetrolCost(petrol);
                    station.setLpgCost(lpg);

                    switch (petrolType) {
                        case "0":
                            list.add(new GasStation(distance, petrol, station.getCoordinates()));
                            break;
                        case "1":
                            list.add(new GasStation(distance, diesel, station.getCoordinates()));
                            break;
                        case "2":
                            list.add(new GasStation(distance, lpg, station.getCoordinates()));
                            break;
                    }
                }

                //sorting list
                Collections.sort(list, new Comparator<GasStation>() {
                    @Override
                    public int compare(GasStation gs1, GasStation gs2) {
                        return Double.compare(gs1.getPosition(), gs2.getPosition());
                    }
                });

                //adding first and last position to list
                list.add(new GasStation(straightDistance * factor, Double.POSITIVE_INFINITY, tempList.get(tempList.size()-1)));
                //list.add(0, new GasStation(0.0, Double.POSITIVE_INFINITY, startCoordinate));

                for(GasStation gs: list){
                    Log.d("list","location: "+gs.getPosition()+" price: "+gs.getFuelCost());
                }

                String startVolume = sharedPreferences.getString(PreferenceTypes.K_FUEL_LEVEL, "8.0");
                String tankVolume = sharedPreferences.getString(PreferenceTypes.K_TANK_CAPACITY, "50.0");
                String avg = sharedPreferences.getString(PreferenceTypes.K_FUEL_CONSUMPTION, "7.0");
                double startV = Double.parseDouble(startVolume);
                double tankV = Double.parseDouble(tankVolume);
                double average = Double.parseDouble(avg);

                //set 4 l minimum at the end of the travel and 5 l minimum as a startvolume
                tankV -= 4.0;
                startV = Math.max(startV, 5.0);

                scaleDistance = ((tankV - startV)/average)*100.0;

                Log.d("maxstopsnumber", "stops: "+maxStopsNumber);
                Algorithm algo = new Algorithm(list, average, tankV, startV, maxStopsNumber);

                algo.getGVSets();

                //set allDistance to fuelResult
                fuelResult = algo.calculateMinimalCost(allDistance, startV);

                return fuelResult;
            }
        });

        try {
            fuelResult = result.get();
        } catch (Exception e) {
            Log.d("FAILED", "FAILED");
        }
        es.shutdown();

        Log.d("RETURN ", String.valueOf(routeInfo.getRouteID()));
        Log.d("COST ", String.valueOf(fuelResult.getCost()));
        return fuelResult;
    }

}
