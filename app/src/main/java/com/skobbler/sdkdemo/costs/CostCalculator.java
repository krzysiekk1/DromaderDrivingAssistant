package com.skobbler.sdkdemo.costs;

import android.content.Context;

import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.costs.fuel.FuelCostCalculator;
import com.skobbler.sdkdemo.costs.tolls.TollsCostCalculator;

/**
 * Created by Krzysiek on 21.12.2016.
 */

public class CostCalculator {

    public double getCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.0;

        FuelCostCalculator fuelCostCalculator = new FuelCostCalculator();
        sum += fuelCostCalculator.getFuelCost(routeInfo, app);

        sum += TollsCostCalculator.getTollsCost(routeInfo, app);

        return sum;
    }

}
