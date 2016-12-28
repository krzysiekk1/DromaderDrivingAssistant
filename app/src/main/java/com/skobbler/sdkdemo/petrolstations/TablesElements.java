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

    public double[] nextFuelLevel;
    public int[] nextVertex;
    public double[] tankFuel;


    public TablesElements(List<GVTuple> gv){

        this.GV = gv;


        this.costs = new double[this.GV.size()];

        this.fuel = new double[this.GV.size()];

        this.size = this.GV.size();

        this.nextFuelLevel = new double[this.size];
        this.nextVertex = new int[this.size];
        this.tankFuel = new double[this.size];

    }

    public void setNextVertex(int vertexNumber, double fuelLevel){
        int result = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                result = i;
            }
        }
        nextVertex[result] = vertexNumber;
    }

    public void setNextFuelLevel(double nextFuelLevel , double fuelLevel){
        int result = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                result = i;
            }
        }
        this.nextFuelLevel[result] = nextFuelLevel;
    }

    public int getNextVertex(double fuelLevel){
        int result = -1;
        int nextVert;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                result = i;
            }
        }
        nextVert = nextVertex[result];
        return nextVert;
    }

    public double getNextFuelLevel(double fuelLevel){
        int result = -1;
        double nextFuelLevel;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                result = i;
            }
        }
        nextFuelLevel = this.nextFuelLevel[result];

        return nextFuelLevel;
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


    public double getVertexCost(double fuelLevel){
        int vertexNumber = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                vertexNumber = i;
            }
        }

        return costs[vertexNumber];
    }


    public void setVertexFuel(double fuelLevel, double settingValue){
        int vertexNumber = -1;

        for(int i = 0; i < size; i++){
            if(GV.get(i).getFuelLevel() == fuelLevel){
                vertexNumber = i;
            }
        }

        costs[vertexNumber] = settingValue;

    }

    public int getSize(){

        return GV.size();
    }


}
