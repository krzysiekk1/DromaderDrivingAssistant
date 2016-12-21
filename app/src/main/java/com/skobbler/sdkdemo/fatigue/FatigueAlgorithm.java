package com.skobbler.sdkdemo.fatigue;


import android.os.Handler;

import com.skobbler.sdkdemo.activity.MapActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

    private FatigueComputations fatigueComputations = new FatigueComputations();
    private MapActivity mapActivityInstance;

    // Handlers stuff
    private boolean repeat = true;
    private Handler myHandler;

    private static final int MEASUREMENT_DELAY_TIME = 300000; // invoke method every 5 minutes - in ms;
    private static final int FIRST_DELAY_TIME = 7200000; // first invoke after 2 hours from the computation beginning - in ms;

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
    private long executionStartTime = System.currentTimeMillis();
    private long executionEndTime;
    private long execution;

    private int hours;
    private int minutes;

    private double executionTime;

    // weather instance
    private WeatherInstance weatherInstance;
    private String weather;

    private boolean response;



    /*
    * Initializing FatigueAlgorithm from MapActivity with its instance
    * */

    public FatigueAlgorithm(MapActivity myMapActivity){
        this.mapActivityInstance = myMapActivity;
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
            /*
            currentLocalTime = cal.getTime();
            localTime = date.format(currentLocalTime);
            */


            // time from beginning measure
            executionEndTime = System.currentTimeMillis();
            execution = executionEndTime - executionStartTime;

            // take minutes
            minutes = (int) execution/60000;

            /* hours = 0;

            while(minutes >= 60){
                minutes -= 60;
                hours++;
            }

            executionTime = (double) hours;

            executionTime += (double) ((minutes * 5) / 3);
            */
            executionTime = (double) minutes;
            // take weather near location - i have to take our coordinates and after that find the nearest place
            weather = weatherInstance.weatherNearLocation();

            //getting response if we should show information about fatigue!!!
            response = fatigueComputations.onCompute(localTime, executionTime, weather);

            

            myHandler.postDelayed(mRunnable, MEASUREMENT_DELAY_TIME);
        }
    };

    //initializing Handler and boolean repeat -> if repeat == false -> delaying FatigueAlgorithm at all

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
