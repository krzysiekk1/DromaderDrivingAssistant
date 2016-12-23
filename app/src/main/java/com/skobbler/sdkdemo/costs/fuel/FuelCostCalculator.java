package com.skobbler.sdkdemo.costs.fuel;

import android.content.Context;

import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithm;

public class FuelCostCalculator {


    public double getFuelCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.0;
        FuelAlgorithm fuelAlgorithm = new FuelAlgorithm(routeInfo, app);

        sum += fuelAlgorithm.getMinimalCost(app);

        return sum;
    }

}