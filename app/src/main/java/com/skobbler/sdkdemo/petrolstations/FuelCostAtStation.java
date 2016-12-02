package com.skobbler.sdkdemo.petrolstations;

import android.content.Context;
import android.database.Cursor;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.sdkdemo.database.ResourcesDAO;

/**
 * Created by Krzysiek on 02.12.2016.
 */

public class FuelCostAtStation {
    private double avgPetrolLiterCost = 2.0;
    private double avgDieselLiterCost = 2.0;
    private double avgLPGLiterCost = 2.0;

    public void calculateFuelCostAtStation(SKCoordinate coordinate, String countryCode, Context app) {

        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance(app);
        resourcesDAO.openDatabase();
        String[] array = new String[] {countryCode};
        String query = "SELECT DISTINCT " + "PetrolCost" + ", " + "DieselCost" + ", " + "LPGCost" +
                        " FROM " + "AvgFuelCosts" + " WHERE " + "CountryCode" + "=?";
        Cursor resultCursor = resourcesDAO.getDatabase().rawQuery(query, array);
        if ((resultCursor != null) && (resultCursor.getCount() > 0)) {
            try {
                resultCursor.moveToFirst();
                avgPetrolLiterCost = Double.parseDouble(resultCursor.getString(0));
                avgDieselLiterCost = Double.parseDouble(resultCursor.getString(1));
                avgLPGLiterCost = Double.parseDouble(resultCursor.getString(2));
            } finally {
                resultCursor.close();
            }
        } else {
            if (resultCursor != null) {
                resultCursor.close();
            }
        }
    }

    public double getAvgPetrolLiterCost() {
        return avgPetrolLiterCost;
    }

    public double getAvgDieselLiterCost() {
        return avgDieselLiterCost;
    }

    public double getAvgLPGLiterCost() {
        return avgLPGLiterCost;
    }

}
