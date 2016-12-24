package com.skobbler.sdkdemo.petrolstations;

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

    public int listSize;


    public DynamicFunction(double tankVolume, GasStationList list, int fillStops) {

        this.tankVolume = tankVolume;
        this.list = list;
        this.fillStops = fillStops;

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
                } else {
                    A[i][0].setVertexFuel(g.getFuelLevel(), Double.POSITIVE_INFINITY);
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
                } else {
                    v.indep(((A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(0.0)) + (d*costU)));
                }

            } else {
                if(A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(tankVolume - d) == Double.POSITIVE_INFINITY){
                    v.indep(Double.POSITIVE_INFINITY);
                } else{
                    v.indep(((A[v.vertexNumber][stopsLeftQ - 1].getVertexCost(tankVolume - d)) + (tankVolume*costU)));
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

                A[vertexU][stopsLeftQ].setVertexFuel(GV.getFuelLevel(), (vertexListR.get(vertexInR).getValue() - (GV.getFuelLevel() * costU)));
                // A[vertexU][stopsLeftQ].fillTupleLists[A[vertexU][stopsLeftQ].getVertexNumber(GV.getFuelLevel())] = vertexListR.get(vertexInR).getFillTupleList();

            }

        }

    }


    public double getBestResult(){

        double bestResult = Double.POSITIVE_INFINITY;

        for (int i = 1; i<fillStops; i++){

            if((A[0][i].getVertexCost(0.0)) < bestResult && (A[0][i].getVertexCost(0.0)) >=0){

                bestResult = A[0][i].getVertexCost(0.0);
            }
        }
        return bestResult;

    }

}
