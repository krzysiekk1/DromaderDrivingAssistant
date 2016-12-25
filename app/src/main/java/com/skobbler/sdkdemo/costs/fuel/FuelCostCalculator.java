package com.skobbler.sdkdemo.costs.fuel;

import android.content.Context;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;
import com.skobbler.sdkdemo.petrolstations.FillStationStructure;
import com.skobbler.sdkdemo.petrolstations.FuelAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class FuelCostCalculator {


    public double getFuelCost (SKRouteInfo routeInfo, Context app) {
        double sum = 0.0;
        FuelAlgorithm fuelAlgorithm = new FuelAlgorithm(routeInfo, app);


        sum += fuelAlgorithm.getMinimalCost(app);
        List<FillStationStructure> fillStationStructureList = new ArrayList<FillStationStructure>();
        fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880664, 19.488134), 2.1, 3.2));
        fillStationStructureList.add(new FillStationStructure(new SKCoordinate(49.880659, 19.488129), 3.4, 5.6));
       // fillStationStructureList.add(new FillStationStructure(new SKCoordinate(34.0, 23.0), 3.4, 5.6));
       // fillStationStructureList.add(new FillStationStructure(new SKCoordinate(37.0, 23.0), 3.4, 5.6));
       // fillStationStructureList.add(new FillStationStructure(new SKCoordinate(70.0, 21.0), 3.4, 5.6));
        SKToolsLogicManager skToolsLogicManager = SKToolsLogicManager.getInstance();
        skToolsLogicManager.setFillStations(fillStationStructureList);

        return sum;
    }

}