package com.skobbler.sdkdemo.costs.fuel;

import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithm;

public class FuelCostCalculator {

    public double getFuelCost (SKRouteInfo routeInfo) {
        double sum = 0.0;
        FuelAlgorithm fuelAlgorithm = new FuelAlgorithm(routeInfo);

        sum += fuelAlgorithm.getMinimalCost();

        return sum;
    }

}