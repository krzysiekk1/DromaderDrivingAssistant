package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 19.12.2016.
 */

public class VertexRange {

    public int vertexNumber;

    public double value;

    public VertexRange(int number){
        this.vertexNumber = number;
    }

    public void indep(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

}

