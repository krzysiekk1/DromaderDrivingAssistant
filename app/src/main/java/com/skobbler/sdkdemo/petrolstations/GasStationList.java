package com.skobbler.sdkdemo.petrolstations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinsendera on 09.12.2016.
 */
public class GasStationList {

    // average consumption of fuel per 100km
    private double avgConsumption;
    public List<GasStation> list;

    /*
    * at the very beginning of the gasstation list has to be the source point with the gas price equals infinity
    * at the end has to be destination point with the gas price equals infinity as well
    *
    * */

    // cost of a fuel on the station
    public List<Double> costFunction = new ArrayList<Double>();

    // distances from the beginning
    public List<Double> distances = new ArrayList<Double>();

    public GasStationList(List<GasStation> mylist, double avg){

        this.avgConsumption = avg;
        this.list = mylist;

        for(int i = 0; i < mylist.size(); i++) {

            // set costFunction to have cost per 1km
            costFunction.add((mylist.get(i).getFuelCost() * avgConsumption) / 100.0);
            distances.add(mylist.get(i).getPosition());

        }

    }

}
