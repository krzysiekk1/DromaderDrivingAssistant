package com.skobbler.sdkdemo.costs.tolls;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKViaPointInfo;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchResultParent;
import com.skobbler.sdkdemo.costs.utils.Country;

import java.util.ArrayList;

/**
 * Created by Krzysiek on 31.08.2016.
 */
public class TollsCostCalculator {

    public int getTollsCost (SKRouteInfo routeInfo) {
        int sum = 0;

        sum += getVignettesCost(routeInfo);
        // sum += getHighwaysCost(routeInfo);

        return sum;
    }

    public int getVignettesCost (SKRouteInfo routeInfo) {
        int sum = 0;

        // create countries list
        ArrayList<String> countries = null;
        ArrayList<SKViaPointInfo> viaPointsInfo = routeInfo.getViaPointsInfo();
        for (SKViaPointInfo viaPointInfo : viaPointsInfo) {
            String newCountry = getCountryCode(viaPointInfo);
            if (countries != null && !countries.contains(newCountry)) {
                countries.add(newCountry);
            }
        }

        for (String countryCode : countries) {
            sum += getVignetteCostByCode(countryCode);
        }

        return sum;
    }

    private String getCountryCode(SKViaPointInfo pointInfo) {
        String countryCode = "NULL";
        SKCoordinate position = pointInfo.getPosition();
        SKSearchResult result = SKReverseGeocoderManager.getInstance().reverseGeocodePosition(position);
        if (result != null) {
            countryCode = result.getName();
        }
        if (result != null && result.getParentsList() != null) {
            for (SKSearchResultParent parent : result.getParentsList()) {
                countryCode = parent.getParentName();
            }
        }
        return countryCode;
    }

    public static int getVignetteCostByCode(String code){
        for (Country e : Country.values()){
            if (code == e.getCode()) return e.getVignetteCost();
        }
        return 0;
    }

}
