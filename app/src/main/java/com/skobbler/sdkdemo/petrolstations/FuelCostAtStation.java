package com.skobbler.sdkdemo.petrolstations;

import com.skobbler.ngx.SKCoordinate;

/**
 * Created by Krzysiek on 02.12.2016.
 */

public class FuelCostAtStation {

    public double getFuelCostAtStation(SKCoordinate coordinate, String countryCode) {
        double avgPetrolLiterCost = 1.5;  // get from database(countryCode)
        double avgDieselLiterCost = 1.4;  // get from database(countryCode)
        double avgLPGLiterCost = 1.1;  // get from database(countryCode)


        return 0.0;
    }

}
