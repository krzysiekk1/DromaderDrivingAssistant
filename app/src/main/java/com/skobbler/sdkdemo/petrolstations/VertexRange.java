package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 19.12.2016.
 */

public class VertexRange {

    public int vertexNumber;
    public double value;
    private double nextFuelLevel;
    private int nextVertex;

    public VertexRange(int number){
        this.vertexNumber = number;
    }

    public void indep(double value){
        this.value = value;
    }
    
    public void setNextFuelLevel(double nextFuelLevel){
        this.nextFuelLevel = nextFuelLevel;
    }

    public void setNextVertex(int nextVertex){
        this.nextVertex = nextVertex;
    }

    public int getNextVertex(){
        return this.nextVertex;
    }

    public double getNextFuelLevel(){
        return this.nextFuelLevel;
    }

    public double getValue(){
        return value;
    }

}
