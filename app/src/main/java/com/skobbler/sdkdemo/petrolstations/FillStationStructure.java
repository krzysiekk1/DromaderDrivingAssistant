package com.skobbler.sdkdemo.petrolstations;

import com.skobbler.ngx.SKCoordinate;

/**
 * Created by marcinsendera on 25.12.2016.
 */

public class FillStationStructure {

    private SKCoordinate coordinates;
    private double fuelToFill;
    private double appCost;

    public FillStationStructure(SKCoordinate skCoordinate, double fuel, double price){
        this.coordinates = skCoordinate;
        this.fuelToFill = fuel;
        this.appCost = price * fuel;
    }

    public SKCoordinate getCoordinates(){
        return this.coordinates;
    }

    public double getFuelToFill(){
        return this.fuelToFill;
    }

    public double getAppCost(){
        return this.appCost;
    }

}
