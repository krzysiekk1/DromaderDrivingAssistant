package com.skobbler.sdkdemo.petrolstations;

import com.skobbler.ngx.SKCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinsendera on 02.12.2016.
 */

public class FuelStationList {

    public List<FuelStationStructure> list = new ArrayList<FuelStationStructure>();
    private int listSize = 0;

    public void addToList(SKCoordinate coordinate){

        //check if it'll be only one this station in my list
        boolean different = true;
        double longitude = coordinate.getLongitude();
        double latitude = coordinate.getLatitude();

        for(int i=0; i < this.list.size(); i++) {
            //returns true if the station is already in the list
            if(this.list.get(i).compareCoordinates(longitude, latitude)){
                different = false;
            }
        }

        if (different){
            list.add(new FuelStationStructure(listSize, coordinate));
            this.listSize++;
        }

    }

}
