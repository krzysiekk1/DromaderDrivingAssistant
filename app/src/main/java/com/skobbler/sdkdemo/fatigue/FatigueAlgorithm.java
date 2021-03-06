package com.skobbler.sdkdemo.fatigue;

import android.content.Context;
import android.os.Handler;

import com.skobbler.sdkdemo.activity.MapActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by marcinsendera on 23.11.2016.
 */

/*
* Algorithm which main goal is to detect or predict the moment when
* driver could be tired and need to stop driving
*
* We want to include several factors to measure:
* - circadian rhythm (mostly based on the current daytime)
* - time from the last stop
* - weather
*
* In our opinion, there are the most significant factor to above-mentioned problem
* and only these can be measured by navigation app
*
* */

public class FatigueAlgorithm {

    //initialization taking most important arguments

    private FatigueComputations fatigueComputations;

    // Handlers stuff
    private boolean repeat = true;
    private Handler myHandler;

    private static final int MEASUREMENT_DELAY_TIME = 300000; // invoke method every 5 minutes - in ms;
    private static final int FIRST_DELAY_TIME = 7200000; // first invoke after 2 hours from the computation beginning - in ms;
    private static final int PAUSE_DELAY_TIME = 1800000; // pause on 30 minutes!

    // timeZone for the start position - this time is driver "inside" time
    private TimeZone timeZone = TimeZone.getDefault();
    private Calendar cal = Calendar.getInstance(timeZone);

    // local datetime
    private Date currentLocalTime;
    private DateFormat date = new SimpleDateFormat("HHmm");

    // different way of doing this
    private int hourOfDay;
    private int minuteOfDay;

    // take the current time for the diver local timezone
    // private String localTime;
    private double localTime;

    // execution time
    private long executionStartTime;
    private long executionEndTime;
    private long execution;

    private int minutes;

    private double executionTime;

    private boolean response;
    private boolean pause;

    /*
    * Initializing FatigueAlgorithm from MapActivity with its instance
    * */
    public FatigueAlgorithm(Context context){
        executionStartTime = System.currentTimeMillis();
        pause = false;

        fatigueComputations = new FatigueComputations(context);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            /** Taking the measures and call onCompute **/
            // localTime measure
            hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            minuteOfDay = cal.get(Calendar.MINUTE);

            localTime = (double) hourOfDay;
            localTime += (double) ((minuteOfDay * 5) / 3);

            // time from beginning measure
            executionEndTime = System.currentTimeMillis();
            execution = executionEndTime - executionStartTime;

            // take minutes
            minutes = (int) execution/60000;

            executionTime = (double) minutes;
            // take weather near location - i have to take our coordinates and after that find the nearest place

            if(pause){
                try {
                    Thread.sleep(PAUSE_DELAY_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pause = false;
            }

            //getting response if we should show information about fatigue!!!
            response = fatigueComputations.onCompute(localTime, executionTime);

            myHandler.postDelayed(mRunnable, MEASUREMENT_DELAY_TIME);
        }
    };

    //initializing Handler and boolean repeat -> if repeat == false -> delaying FatigueAlgorithm at all

    public boolean getResponse(){
        return this.response;
    }

    public void setResponse(boolean response){
        this.response = response;
    }

    public void takeBreak(){
        executionStartTime = System.currentTimeMillis();
        response = false;
    }

    public void dismiss(){
        executionStartTime = System.currentTimeMillis();
        pause = true;
        response = false;
    }

    public void startMeasurement() {

        myHandler = new Handler();
        myHandler.postDelayed(mRunnable, FIRST_DELAY_TIME);

        /*
        * if the driver change repeat statement to false,
        * the handler won't be used anytime
        * */

        if(!repeat) {
            myHandler.removeCallbacks(mRunnable);
        }
    }

}
