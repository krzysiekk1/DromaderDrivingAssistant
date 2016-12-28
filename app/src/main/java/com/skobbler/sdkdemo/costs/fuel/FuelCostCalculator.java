package com.skobbler.sdkdemo.costs.fuel;

import android.content.Context;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;
import com.skobbler.sdkdemo.petrolstations.FillStationStructure;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithm;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithmResult;

import java.util.ArrayList;
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

        if(number == 0) {
            fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880664, 19.488134), 2.1, 3.2));
            fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880659, 19.488129), 3.4, 5.6));
        } else if(number == 1){
            fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880664, 19.488134), 30.2, 4.3));
            fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880659, 19.488129), 0.1, 0.1));
            fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880664, 19.488134), 0.2, 0.2));
        } else if(number == 2){

        }
        SKToolsLogicManager skToolsLogicManager = SKToolsLogicManager.getInstance();
        skToolsLogicManager.setFillStations(fillStationStructureList, number);


        return sum;
    }

}