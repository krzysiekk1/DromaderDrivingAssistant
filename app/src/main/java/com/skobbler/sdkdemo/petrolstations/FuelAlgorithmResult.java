package com.skobbler.sdkdemo.petrolstations;

import java.util.List;

/**
 * Created by marcinsendera on 27.12.2016.
 */

public class FuelAlgorithmResult {

    private List<FillStationStructure> fillStationStructureList;

    private double cost;

    public FuelAlgorithmResult(double resultCost, List<FillStationStructure> list){
        this.fillStationStructureList = list;
        this.cost = resultCost;
    }

    public List<FillStationStructure> getList(){
        return this.fillStationStructureList;
    }

    public void addCost(double newCost){
        this.cost += newCost;
    }

    public double getCost(){
        return this.cost;
    }

    public void setFillStationStructureList(List<FillStationStructure> list){
        this.fillStationStructureList = list;
    }

}
