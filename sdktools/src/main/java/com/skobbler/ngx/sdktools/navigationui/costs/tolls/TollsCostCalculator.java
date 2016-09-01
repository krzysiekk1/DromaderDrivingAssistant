package com.skobbler.ngx.sdktools.navigationui.costs.tolls;

import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.sdktools.navigationui.costs.utils.Country;

import java.util.List;

public class TollsCostCalculator {

    public static double getTollsCost (SKRouteInfo routeInfo) {
        double sum = 0.0;

        // sum += getRoadCost(routeInfo);

        if (!routeInfo.isContainsHighWays()) {
            return sum;
        }

        sum += getVignettesCost(routeInfo);

        return sum;
    }

    private static double getVignettesCost (SKRouteInfo routeInfo) {
        double sum = 0.0;

        int routeID = routeInfo.getRouteID();
        List<String> countries = SKRouteManager.getInstance().getCountriesTraversedByRouteByUniqueId(routeID);

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
        return 0.0;
    }

}
