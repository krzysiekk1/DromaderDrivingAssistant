package com.skobbler.sdkdemo.costs.fuel;

import android.content.Context;
import android.util.Log;

import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;
import com.skobbler.sdkdemo.petrolstations.FillStationStructure;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithm;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithmResult;

import java.util.List;

/**
 * Created by Krzysiek
 */

public class FuelCostCalculator {

    public double getFuelCost (SKRouteInfo routeInfo, Context app, int number) {
        double sum = 0.0;
        FuelAlgorithm fuelAlgorithm = new FuelAlgorithm(routeInfo, app);

        FuelAlgorithmResult fuelAlgorithmResult = fuelAlgorithm.getMinimalCost();
        sum += fuelAlgorithmResult.getCost();

        List<FillStationStructure> fillStationStructureList = fuelAlgorithmResult.getList();
        for(FillStationStructure fss: fillStationStructureList){
            Log.d("fillstationstructure","coordinates: "+fss.getCoordinates().toString()+" fuel: "+fss.getFuelToFill()+" price: "+fss.getAppCost());
        }
        SKToolsLogicManager skToolsLogicManager = SKToolsLogicManager.getInstance();
        skToolsLogicManager.setFillStations(fillStationStructureList, number);

        return sum;
    }

}
