package com.skobbler.sdkdemo.petrolstations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinsendera on 27.12.2016.
 */

public class FuelAlgorithmResult {

    private List<Integer> vertices;

    private List<Double> fuelLevels;
    private List<Double> tanks;
    public List<Double> costs;

    private GasStationList gasStationList;

    private double average;

    private List<FillStationStructure> fillStationStructureList;

    private double cost;

    private double endVolume;

    public FuelAlgorithmResult(double resultCost, List<Integer> vert, List<Double> fuels, GasStationList list, double avg, double distance, double startVolume){

        this.cost = resultCost;
        this.vertices = vert;
        this.fuelLevels = fuels;
        this.gasStationList = list;
        this.average = avg;

        tanks = new ArrayList<Double>();
        costs = new ArrayList<Double>();

        for(int i = 0; i<vertices.size()-1; i++){
            if(i == vertices.size()-2){
                double distToTank = ((0.0 + (this.gasStationList.distances.get
                        (vertices.get(i+1)) - this.gasStationList.distances.get(vertices.get(i)))) - fuelLevels.get(i));
                double cost = this.gasStationList.list.get(vertices.get(i)).getFuelCost();
                distToTank = (((distToTank*average)/100.0));
                tanks.add(distToTank);
                costs.add(distToTank*cost);
            }
            double distToTank = ((fuelLevels.get(i+1) + (this.gasStationList.distances.get
                    (vertices.get(i+1)) - this.gasStationList.distances.get(vertices.get(i)))) - fuelLevels.get(i));
            double cost = this.gasStationList.list.get(vertices.get(i)).getFuelCost();
            distToTank = (((distToTank*average)/100.0));
            tanks.add(distToTank);
            costs.add(distToTank*cost);
        }

        this.fillStationStructureList = new ArrayList<FillStationStructure>();
        for(int i = 0; i < vertices.size() - 1; i++){
            fillStationStructureList.add(new FillStationStructure(this.gasStationList.list.get
                                        (vertices.get(i)).getCoordinate(), tanks.get(i), costs.get(i)));
        }


        if (this.cost == 0.0){
            this.endVolume = startVolume - ((distance*average)/100.0);
        } else {
            this.endVolume = 4.0;
        }

    }

    public List<FillStationStructure> getList(){
        return this.fillStationStructureList;
    }

    public double getCost(){
        return this.cost;
    }
    
    public double getEndVolume(){
        return this.endVolume;
    }

}
