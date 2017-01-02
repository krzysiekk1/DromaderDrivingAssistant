package com.skobbler.sdkdemo.petrolstations;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by marcinsendera on 19.12.2016.
 */

public class DynamicFunction {

    private double tankVolume;
    private GasStationList list;

    private int fillStops;

    public TablesElements[][] A;

    private int listSize;

    private double avg;

    private double alldistance;
    private double startVolume;

    public DynamicFunction(double tankVolume, GasStationList list, int fillStops, double average, double alldistance, double startVolume) {

        this.alldistance = alldistance;
        this.startVolume = startVolume;
        this.tankVolume = tankVolume;
        this.list = list;
        this.fillStops = fillStops;
        this.avg = average;
        this.listSize = this.list.list.size();
        this.A = new TablesElements[listSize][fillStops];

        for(int i = 0; i< this.list.list.size(); i++){
            for(int j=0; j< this.fillStops; j++){
                List<GVTuple> mylist = this.list.list.get(i).GV;
                A[i][j] = new TablesElements(mylist);
            }
        }

        int lastElement  = listSize - 1;
        double lastElementDistance = this.list.distances.get(lastElement);
        double distance;

        for(int i = 0; i < listSize - 1; i++){
            distance = lastElementDistance - this.list.distances.get(i);
            for(GVTuple g: A[i][0].GV) {
                if (distance <= this.tankVolume && g.getFuelLevel() <= this.tankVolume) {
                    A[i][0].setVertexFuel(g.getFuelLevel(), ((distance - g.getFuelLevel()) * (this.list.costFunction.get(i))));
                    A[i][0].setNextVertex(listSize - 1, g.getFuelLevel());
                    A[i][0].setNextFuelLevel((distance - g.getFuelLevel()), g.getFuelLevel());
                } else {
                    A[i][0].setVertexFuel(g.getFuelLevel(), Double.POSITIVE_INFINITY);
                    A[i][0].setNextVertex(- 1, g.getFuelLevel());
                    A[i][0].setNextFuelLevel(-1.0, g.getFuelLevel());
                }
            }
        }
    }

    public void fillRow(int vertexU, int stopsLeftQ){

        ArrayList<VertexRange> vertexListR = new ArrayList<VertexRange>();

        double distanceU = list.distances.get(vertexU);
        double costU = list.costFunction.get(vertexU);

        for(int i = vertexU +1; i < listSize; i++){
            if ((list.distances.get(i) - distanceU) <= tankVolume){
                vertexListR.add(new VertexRange(i));
            }
        }

        for(VertexRange v: vertexListR){
            double d = list.distances.get(v.vertexNumber) - distanceU;
            if((list.costFunction.get(v.vertexNumber) <= costU) || (stopsLeftQ == 1)  ){
                if(A[v.vertexNumber][stopsLeftQ -1].getVertexCost(0.0) == Double.POSITIVE_INFINITY){
                    v.indep(Double.POSITIVE_INFINITY);
                    v.setNextVertex(-1);
                    v.setNextFuelLevel(-1.0);
                } else {
                    v.indep(((A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(0.0)) + (d*costU)));
                    v.setNextVertex(v.vertexNumber);
                    v.setNextFuelLevel(0.0);
                }

            } else {
                if(A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(tankVolume - d) == Double.POSITIVE_INFINITY){
                    v.indep(Double.POSITIVE_INFINITY);
                    v.setNextVertex(-1);
                    v.setNextFuelLevel(-1.0);
                } else{
                    v.indep(((A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(tankVolume - d)) + (tankVolume*costU)));
                    v.setNextVertex(v.vertexNumber);
                    v.setNextFuelLevel(tankVolume - d);
                }
            }
        }

        // sorting
        Collections.sort(vertexListR, new Comparator<VertexRange>() {
            @Override
            public int compare(VertexRange v1, VertexRange v2) {
                return Double.compare(v1.value, v2.value);
            }
        });

        int vertexInR = 0;

        for(GVTuple GV: list.list.get(vertexU).GV){
            if(vertexListR.size()!=0) {
                while (GV.getFuelLevel() > (list.distances.get(vertexListR.get(vertexInR).vertexNumber) - distanceU)) {
                    // increasing v value
                    if (vertexListR.size() > (vertexInR + 1)) {
                        vertexInR++;
                    } else {
                        break;
                    }
                }
                A[vertexU][stopsLeftQ].setVertexFuel(GV.getFuelLevel(),
                        (vertexListR.get(vertexInR).getValue() - (GV.getFuelLevel() * costU)));
                A[vertexU][stopsLeftQ].setNextFuelLevel(vertexListR.get(vertexInR).getNextFuelLevel(), GV.getFuelLevel());
                A[vertexU][stopsLeftQ].setNextVertex(vertexListR.get(vertexInR).getNextVertex(), GV.getFuelLevel());
            }
        }
    }

    public FuelAlgorithmResult getBestResult(){

        double bestResult = Double.POSITIVE_INFINITY;
        int bestRoute = -1;
        List<Integer> vertices = new ArrayList<Integer>();
        List<Double> fuels = new ArrayList<Double>();

        for (int i = 1; i<fillStops; i++){
            if((A[0][i].getVertexCost(0.0)) < bestResult && (A[0][i].getVertexCost(0.0)) >=0){
                Log.d("results", "result: "+A[0][i].getVertexCost(0.0));
                bestResult = A[0][i].getVertexCost(0.0);
                bestRoute = i;
            }
        }
        if(bestRoute != -1){
            int nextVert = A[0][bestRoute].getNextVertex(0.0);
            double nextFuel = A[0][bestRoute].getNextFuelLevel(0.0);
            vertices.add(nextVert);
            fuels.add(nextFuel);
            bestRoute--;
            while(bestRoute >= 0 && A[nextVert][bestRoute].getVertexNumber(nextFuel) != -1){
                int nextVert1 = A[nextVert][bestRoute].getNextVertex(nextFuel);
                double nextFuel1 = A[nextVert][bestRoute].getNextFuelLevel(nextFuel);
                vertices.add(nextVert1);
                fuels.add(nextFuel1);

                nextVert = nextVert1;
                nextFuel = nextFuel1;
                bestRoute--;
            }
        }

        FuelAlgorithmResult result = new FuelAlgorithmResult(bestResult, vertices, fuels, list, avg, alldistance, startVolume);
        return result;
    }

}
