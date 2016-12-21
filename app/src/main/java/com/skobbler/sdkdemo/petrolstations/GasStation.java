package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 21.12.2016.
 */
import java.util.ArrayList;
import java.util.List;

public class GasStation {

    private double fuelcost;
    private double position;

    public List<GVTuple> GV = new ArrayList<GVTuple>();

    public GasStation(double pos, double cost){

        this.fuelcost = cost;
        this.position = pos;

    }


    public void addGVTuple(double fuelLevel, int previousStationPosition){
        this.GV.add(new GVTuple(fuelLevel, previousStationPosition));
    }



    public double getFuelCost(){
        return this.fuelcost;
    }

    public double getPosition(){
        return this.position;
    }


}

