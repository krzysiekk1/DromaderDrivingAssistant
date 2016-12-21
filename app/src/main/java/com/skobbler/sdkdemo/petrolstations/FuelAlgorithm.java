package com.skobbler.sdkdemo.petrolstations;

import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKNearbySearchSettings;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchStatus;
import com.skobbler.ngx.util.SKLogging;


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

    private SKCoordinate startCoordinates;
    private SKRouteInfo routeInfo;

    public FuelAlgorithm(SKRouteInfo routeInfo){

        this.routeInfo = routeInfo;

        double allDistance = routeInfo.getDistance()/1000.0;
        double straightDistance;

        int routeID = routeInfo.getRouteID();

       // this.startCoordinates = startCoordinates;
        
        this.stationList = new FuelStationList();

        //getting coordinates across whole route
        List<SKCoordinate> tempList = new ArrayList<SKCoordinate>();
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);


        for (SKExtendedRoutePosition pos : positions){
            tempList.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
        }



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



    }

    @Override
    public void onReceivedSearchResults(final List<SKSearchResult> searchResults){
        addToFuelStationList(searchResults);
    }


    private void addToFuelStationList(List<SKSearchResult> searchResults) {
        for (SKSearchResult result : searchResults) {
            stationList.addToList(result.getLocation());
        }
    }


    public double getMinimalCost() {




        return 2.0;
    }

}
