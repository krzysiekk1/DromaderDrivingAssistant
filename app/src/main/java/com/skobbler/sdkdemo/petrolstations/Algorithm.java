package com.skobbler.sdkdemo.petrolstations;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by marcinsendera on 09.12.2016.
 */
public class Algorithm {

    private List<GasStation> stations;
    private GasStationList stationList;

    private double avgConsumption;
    private double tankVolume;
    private double startVolume;


    private int maxStops;

    private DynamicFunction dynamic;

    private double scaleDistance;

    public Algorithm (List<GasStation> list, double avgcons, double tankV, double startV, int maxStops){

        this.maxStops = maxStops;

        this.stations = list;
        this.avgConsumption = avgcons;
        this.tankVolume = (tankV/avgConsumption)*100.0;
        this.startVolume = startV;

        this.scaleDistance = ((tankV-startV)/avgConsumption)*100.0;

        for(GasStation station: this.stations){
            station.scalePosition(scaleDistance);
        }

        this.stations.add(0, new GasStation(0.0, 0.0));


        this.stationList = new GasStationList(this.stations, avgConsumption);

        for(GasStation gs: this.stations){
            Log.d("list-algo","location: "+gs.getPosition()+" price: "+gs.getFuelCost());
        }

    }

    public void getGVSets(){
        /*
        * creating GVList for every GasStation in GasStationList - which in fact may low the complexity of an algorithm
        *
        * let's define the GVSet:
        *
        * For every u in Vertices the GV(u) = {U - d(w,u)| w in Vertices and c(w) < c(u) and d(w,u) <= U} sum {0}
        *
        * Because a graph is a fixed path, so we should analise only vertices before the current station! + adding 0 obviously
        * */

        for(int i = 0; i < this.stationList.list.size(); i++){

            //adding 0 to GVTuple
            this.stationList.list.get(i).addGVTuple(0, 0);

            for(int j = 0; j < i; j++){

                //adding to GV Set, taking info from stationList.costFunction and .distances
                if(stationList.costFunction.get(j) < stationList.costFunction.get(i)){

                    if(Math.abs(stationList.distances.get(i) - stationList.distances.get(j)) <= tankVolume){
                        this.stationList.list.get(i).addGVTuple((tankVolume - Math.abs(stationList.distances.get(i) - stationList.distances.get(j)) ), j);
                    }
                }


                // sorting
                Collections.sort(this.stationList.list.get(i).GV, new Comparator<GVTuple>(){

                    @Override
                    public int compare(GVTuple gv1, GVTuple gv2) {
                        return Double.compare(gv1.getFuelLevel(), gv2.getFuelLevel());
                    }
                });
            }


        }

    }


    public FuelAlgorithmResult calculateMinimalCost(){

        this.dynamic = new DynamicFunction(this.tankVolume, this.stationList, this.maxStops);

        for(int j = 1; j <maxStops; j++){
            for(int i = 0; i < stationList.list.size(); i++){

                dynamic.fillRow(i, j);
            }
        }

        return dynamic.getBestResult();

    }



}
