package com.skobbler.sdkdemo.petrolstations;


import java.util.List;

/**
 * Created by marcinsendera on 14.12.2016.
 */
public class TablesElements {

    public List<GVTuple> GV;

    public double[] costs;

    public double[] fuel;

    public int size;

    public TablesElements(List<GVTuple> gv){

        this.GV = gv;


        this.costs = new double[this.GV.size()];

        this.fuel = new double[this.GV.size()];

        this.size = this.GV.size();
       // System.out.println("this.GV.size: "+ this.size);

    }

    public int getVertexNumber(double fuelLevel){
        int result = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                result = i;
            }
        }

        return result;
    }

    public double getVertexFuel(double fuelLevel){
        int vertexNumber = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                vertexNumber = i;
            }
        }

        return fuel[vertexNumber];

    }

    public void setVertexFuel(double fuelLevel, double settingValue){
        int vertexNumber = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                vertexNumber = i;
            }
        }

        fuel[vertexNumber] = settingValue;

    }

    public int getSize(){

        return GV.size();
    }
    //public void setFuel(double )


}

