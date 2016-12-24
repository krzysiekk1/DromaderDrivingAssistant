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
import java.util.List;

/**
 * Created by marcinsendera on 02.12.2016.
 */

public class FuelAlgorithm implements SKSearchListener{

    private static final short RADIUS = 2000;
    private static final int[] SEARCH_CATEGORY = new int[] {SKCategories.SKPOICategory.SKPOI_CATEGORY_FUEL.getValue()};

    /*
    density of taking the coordinates across route
    67 it's about coordinate every ~ 2km
     */

    private static final int DENSITY = 67;
    private static final int START_COORDINATE_NR = 0;

    private List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();

    private List<GasStation> list = new ArrayList<GasStation>();

    private FuelStationList stationList = new FuelStationList();

    private SKCoordinate startCoordinate;
    private SKRouteInfo routeInfo;


    private double factor;

    private Context app;

    private int maxStopsNumber;

    private double straightDistance;

    private double scaleDistance;

    public FuelAlgorithm(SKRouteInfo routeInfo, Context app){


        this.app = app;
        this.routeInfo = routeInfo;

        double allDistance = routeInfo.getDistance()/1000.0;


        this.maxStopsNumber = (int) allDistance/300;
        if(this.maxStopsNumber == 0 || this.maxStopsNumber == 1){
            this.maxStopsNumber = 2;
        }

        int routeID = routeInfo.getRouteID();

       // this.startCoordinates = startCoordinates;
        
        this.stationList = new FuelStationList();

        //getting coordinates across whole route
        List<SKCoordinate> tempList = new ArrayList<SKCoordinate>();
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);


        for (SKExtendedRoutePosition pos : positions){
            tempList.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
        }


        straightDistance = SKToolsUtils.distanceBetween(positions.get(0).getCoordinate(), positions.get(positions.size() - 1).getCoordinate()) / 1000.0;
        startCoordinate = positions.get(0).getCoordinate();

        factor = straightDistance/allDistance;


        int END_COORDINATE_NR = tempList.size() - 1;

        for (int coordinateNr = START_COORDINATE_NR + 1; coordinateNr < END_COORDINATE_NR; coordinateNr += DENSITY){
            coordinates.add(tempList.get(coordinateNr));

            SKSearchManager searchManager = new SKSearchManager(this);
            SKNearbySearchSettings searchObject = new SKNearbySearchSettings();
            searchObject.setRadius(this.RADIUS);
            searchObject.setSearchCategories(this.SEARCH_CATEGORY);
            searchObject.setSearchResultsNumber(100);
            searchObject.setSearchTerm("");
            searchObject.setSearchMode(SKSearchManager.SKSearchMode.OFFLINE);

            searchObject.setLocation(tempList.get(coordinateNr));

            SKSearchStatus status = searchManager.nearbySearch(searchObject);

            if (status != SKSearchStatus.SK_SEARCH_NO_ERROR) {
                SKLogging.writeLog("SKSearchStatus: ", status.toString(), 0);
            }
        }

       // this.changeLists();

    }

    @Override
    public void onReceivedSearchResults(final List<SKSearchResult> searchResults){
        addToFuelStationList(searchResults);
        Log.d("myTag", "Found: "+searchResults.size()+" maxStopsNumber: "+maxStopsNumber);
    }


    private void addToFuelStationList(List<SKSearchResult> searchResults) {
        for (SKSearchResult result : searchResults) {
            stationList.addToList(result.getLocation());
        }

    }


    public void changeLists(){


        //TODO Add first and last point to stationList!!!!!!!!!!

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
        String petrolType = sharedPreferences.getString(PreferenceTypes.K_FUEL_TYPE, "0");


        for(FuelStationStructure station: this.stationList.list){

            String countryCode = null;

            SKSearchResult searchResult = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(station.getCoordinates());

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



            double distance = SKToolsUtils.distanceBetween(startCoordinate, station.getCoordinates()) * factor;
            station.setDieselCost(diesel);
            station.setPetrolCost(petrol);
            station.setLpgCost(lpg);

            if(petrolType.equals("0")){
                list.add(new GasStation(distance, petrol));
            } else if (petrolType.equals("1")){
                list.add(new GasStation(distance, diesel));
            } else if (petrolType.equals("2")){
                list.add(new GasStation(distance, lpg));
            }


        }


        //adding first and last position to list
        list.add(new GasStation(this.straightDistance, Double.POSITIVE_INFINITY));
        list.add(0, new GasStation(0.0, Double.POSITIVE_INFINITY));

    }


    public double getMinimalCost(Context app) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);

        double cost = 2.0;

        String startVolume = sharedPreferences.getString(PreferenceTypes.K_FUEL_LEVEL, "8.0");
        String tankVolume = sharedPreferences.getString(PreferenceTypes.K_TANK_CAPACITY, "50.0");
        String avg = sharedPreferences.getString(PreferenceTypes.K_FUEL_CONSUMPTION, "7.0");
        double startV = Double.parseDouble(startVolume);
        double tankV = Double.parseDouble(tankVolume);
        double average = Double.parseDouble(avg);

        this.scaleDistance = ((tankV - startV)/average)*100.0;

        List<GasStation> list1 = new ArrayList<GasStation>();
        list1.add(new GasStation(0.0, 0.0));
        list1.add(new GasStation(123.4, 4.3));
        list1.add(new GasStation(134.7, 3.8));
        list1.add(new GasStation(195.8, 4.15));
        list1.add(new GasStation(223.4, 3.56));
        list1.add(new GasStation(256.7, 4.35));
        list1.add(new GasStation(387.2, 4.0));
        list1.add(new GasStation(547.0, 5.6));
        list1.add(new GasStation(623.0, 5.3));
        list1.add(new GasStation(785.2, 7.3));
        list1.add(new GasStation(843.1, 3.92));
        list1.add(new GasStation(934.2, 4.16));
        list1.add(new GasStation(986.4, 4.44));
        list1.add(new GasStation(1000.7, 4.22));
        list1.add(new GasStation(1002, 0.24));
        list1.add(new GasStation(1076.3, 4.13));
        list1.add(new GasStation(1156.2, 3.98));
        list1.add(new GasStation(1342.3, 4.03));
        list1.add(new GasStation(1789.2, 5.65));
        list1.add(new GasStation(2222.2, 3.23));
        list1.add(new GasStation(2489.5, 2.45));
        list1.add(new GasStation(2589.5, 3.85));
        list1.add(new GasStation(2769.5, 5.43));
        list1.add(new GasStation(2876.2, 1.23));
        list1.add(new GasStation(3000.0, 4.87));
        list1.add(new GasStation(3210.2, 8.00));
        list1.add(new GasStation(3500.0, 0.00));

        Algorithm algo = new Algorithm(list1, average, tankV, startV, 12);

        algo.getGVSets();

        cost = algo.calculateMinimalCost();


        return cost;
    }

}
