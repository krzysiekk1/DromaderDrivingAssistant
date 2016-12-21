package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 21.12.2016.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

       // System.out.println("1st size: "+listSize+"2nd size: "+fillStops);
        for(int i = 0; i< this.list.list.size(); i++){


         //   System.out.println("mylist size: "+this.list.list.get(i).GV.size());
         //   System.out.println("");
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

          //  System.out.println("For loop 1");
            for(GVTuple g: A[i][1].GV){
                System.out.println("For loop 2");
                if(distance <= this.tankVolume && g.getFuelLevel() <= this.tankVolume){
                    A[i][1].setVertexFuel(g.getFuelLevel(), ((distance - g.getFuelLevel())*(this.list.costFunction.get(i))));
                } else {
                    A[i][1].setVertexFuel(g.getFuelLevel(), Double.POSITIVE_INFINITY);
                }


            }
        }


    }


    public void fillRow(int vertexU, int stopsLeftQ){

        ArrayList<VertexRange> vertexListR = new ArrayList<VertexRange>();

        double distanceU = list.distances.get(vertexU);
        double costU = list.costFunction.get(vertexU);

        for(int i = vertexU + 1; i < listSize; i++){
            if ((list.distances.get(i) - distanceU) <= tankVolume){
                vertexListR.add(new VertexRange(i));
            }
        }
     //   System.out.println("1");

        for(VertexRange v: vertexListR){
        //    System.out.println("11");
            double d = list.distances.get(v.vertexNumber) - distanceU;
        //    System.out.println("12");
            if(list.costFunction.get(v.vertexNumber) <= costU){
                v.indep((A[v.vertexNumber][stopsLeftQ - 1].getVertexFuel(0.0)) + (d*costU));//System.out.println("if");
            } else {
                v.indep((A[v.vertexNumber][stopsLeftQ - 1].getVertexFuel(tankVolume - d)) + (tankVolume*costU));//System.out.println("else");
            }

        }

        // sorting
        Collections.sort(vertexListR, new Comparator<VertexRange>() {
            @Override
            public int compare(VertexRange v1, VertexRange v2) {
                return Double.compare(v1.value, v2.value);
            }
        });

       // System.out.println("2");

        int vertexInR = 0;

        for(GVTuple GV: list.list.get(vertexU).GV){
           // System.out.println("3");

            if(vertexListR.size()!=0) {
                while (GV.getFuelLevel() > (list.distances.get(vertexListR.get(vertexInR).vertexNumber) - distanceU)) {
              //      System.out.println("4");
                    // increasing v value

                    if (vertexListR.size() > (vertexInR + 1)) {
                        vertexInR++;
                    } else {
                        break;
                    }
                }

                System.out.println(vertexListR.get(vertexInR).getValue() - (GV.getFuelLevel() * costU));
                A[vertexU][stopsLeftQ].setVertexFuel(GV.getFuelLevel(), (vertexListR.get(vertexInR).getValue() - (GV.getFuelLevel() * costU)));
            }

        }

    }


    public double getBestResult(){

        double bestResult = Double.POSITIVE_INFINITY;

        for (int i = 1; i<fillStops; i++){
          //  System.out.println("getBestResult: i = "+ i);

          //  System.out.println(A[0][i].getSize());
          //  System.out.println(A[0][i].getVertexFuel(0.0));

            if((A[0][i].getVertexFuel(0.0)) < bestResult){

                bestResult = A[0][i].getVertexFuel(0.0);
            }
        }

        return bestResult;

    }

}

