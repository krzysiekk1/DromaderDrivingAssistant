package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 21.12.2016.
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algorithm {

        private Graph graph;
        private List<GasStation> stations;
        private GasStationList stationList;

        private double avgConsumption;
        private double tankVolume;
        private double startVolume;

        private int stationNumber;

        private int maxStops;

        private DynamicFunction dynamic;

        public Algorithm (List<GasStation> list, double avgcons, double tankV, double startV, int stations, int maxStops){

            this.maxStops = maxStops;

            this.stations = list;
            this.avgConsumption = avgcons;
            this.tankVolume = (tankV/avgConsumption)*100.0;
            this.startVolume = startV;
            this.stationNumber = stations;

            this.stationList = new GasStationList(this.stations, avgConsumption);
            this.graph = new Graph(stationNumber, tankVolume, startVolume, stationList);



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
                this.stationList.list.get(i).addGVTuple(0, Integer.MIN_VALUE);

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


        public double calculateMinimalCost(){

            this.dynamic = new DynamicFunction(this.tankVolume, this.stationList, this.maxStops);





            for(int i = 0; i < stationList.list.size(); i++){
                for(int j = 1; j <maxStops; j++){
                    dynamic.fillRow(i, j);
                }
            }

/*
        ArrayList<Double> costList = new ArrayList<Double>();
        for(int  i = 0; i <this.maxStops; i++){
            System.out.println("calculateMinimalCost: i = "+i);
            costList.add(this.dynamic.A(1, i+1, 0.0));
        }

        return (double) Collections.min(costList);

*/

            double bestResult = dynamic.getBestResult();
            System.out.println(bestResult);
            return dynamic.getBestResult();

        }







}
