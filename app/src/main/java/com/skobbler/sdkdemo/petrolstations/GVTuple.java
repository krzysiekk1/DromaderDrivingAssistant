package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 13.12.2016.
 */

public class GVTuple {

    private double fuelLevel;

    private int previousStation;

    public GVTuple(double fuelLevel, int previousStation){
        this.fuelLevel = fuelLevel;
        this.previousStation = previousStation;
    }

    public double getFuelLevel(){
        return this.fuelLevel;
    }

    public int getPreviousStationPosition(){
        return this.previousStation;
    }

}
