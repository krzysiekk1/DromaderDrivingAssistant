package com.skobbler.sdkdemo.fatigue;

import android.content.Context;

/**
 * Created by marcinsendera on 24.11.2016.
 */

public class FatigueComputations {

    // instance of FuzzyLogicClass
    private FuzzyLogicClass fuzzyLogic;

    // my factors/arguments that have been received from upper FatigueAlgorithm instance
    //private String myLocalTime;
    private double myLocalTime;

    // changing received data
    private double myExecutionTime;

    public FatigueComputations(Context context) {
        // handling exception while creating a new instance of a FuzzyLogicClass
        try {
            this.fuzzyLogic = new FuzzyLogicClass(context);
        } catch (FCLFileCannotBeOpenedException e) {
            e.printStackTrace();
        }
    }

    public boolean onCompute(double localTime, double executionTime){

        this.myLocalTime = localTime;
        this.myExecutionTime = executionTime;

        double response = this.fuzzyLogic.getValue(this.myLocalTime, this.myExecutionTime);

        boolean sendMessage;

        if(response >= 10.0){
            // recognized as fatigue
            sendMessage = true;
        }
        else {
            // driver can
            sendMessage = false;
        }

        return sendMessage;
    }

}
