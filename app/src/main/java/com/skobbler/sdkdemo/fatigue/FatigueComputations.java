package com.skobbler.sdkdemo.fatigue;

/**
 * Created by marcinsendera on 24.11.2016.
 */

public class FatigueComputations {

    // instance of FuzzyLogicClass
    private FuzzyLogicClass fuzzyLogic = new FuzzyLogicClass();

    // my factors/arguments that have been received from upper FatigueAlgorithm instance
    //private String myLocalTime;
    private double myLocalTime;

    private String myWeather;

    // changing received data

    private double myExecutionTime;


    public void onCompute(double localTime, double executionTime, String weather){

        this.myLocalTime = localTime;

        this.myExecutionTime = executionTime;

        this.myWeather = weather;





    }


}
