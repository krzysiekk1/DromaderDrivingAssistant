package com.skobbler.sdkdemo.fatigue;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.search.SKNearbySearchSettings;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchStatus;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;

import java.util.List;

/**
 * Created by Krzysiek on 21.12.2016.
 */

public class HotelSearch implements SKSearchListener {
    private static final int[] searchCategories = new int[] {
            SKCategories.SKPOICategory.SKPOI_CATEGORY_HOTEL.getValue()
    };

    short radius = 32000;   // 32 km

    SKCoordinate hotelCoordinate = null;
    SKSearchManager searchManager;
    SKNearbySearchSettings searchObject;
    SKPosition currentPosition;
    SKCoordinate currentCoordinate;
    SKSearchStatus status;

    public void startSearch() {
        searchManager = new SKSearchManager(this);
        searchObject = new SKNearbySearchSettings();
        currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(true);
        currentCoordinate = currentPosition.getCoordinate();
        searchObject.setLocation(currentCoordinate);
        searchObject.setRadius(radius);
        searchObject.setSearchResultsNumber(1);
        searchObject.setSearchCategories(searchCategories);
        searchObject.setSearchTerm(""); // all
        searchObject.setSearchMode(SKSearchManager.SKSearchMode.OFFLINE);
        Log.d("myTag","status1");
        status = searchManager.nearbySearch(searchObject);
        Log.d("myTag","status2");
        if (status != SKSearchStatus.SK_SEARCH_NO_ERROR) {
            SKLogging.writeLog("SKSearchStatus: ", status.toString(), 0);

        }
        Log.d("myTag","status3");
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("myTag", "HotelSearch is main thread");

            // Current Thread is Main Thread.
        }
    }


    @Override
    public void onReceivedSearchResults(List<SKSearchResult> searchResults) {

            Log.d("myTag","on received search results");
            if (searchResults.size() > 0) {
                hotelCoordinate = searchResults.get(0).getLocation();
                Log.d("myTag","1st find hotel coord" + hotelCoordinate.toString());
                SKToolsLogicManager skToolsLogicManager = SKToolsLogicManager.getInstance();
                skToolsLogicManager.setHotelCoordinates(hotelCoordinate);
            }

    }


}
