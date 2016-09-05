package com.skobbler.ngx.sdktools.onebox.fragments;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.skobbler.ngx.R;
import com.skobbler.ngx.SKCoordinate;

/**
 * This class handles the logic for current position.
 */
public class OneBoxManager {
    public static final String ONEBOX_FRAGMENT_ID = "OneBoxFragment";
    public static final int SORT_DISTANCE = 0, SORT_RANK = 1, SORT_NAME = 2;
    /**
     * Current position
     */
    private static SKCoordinate currentPosition;

    /**
     * Set current position
     * @param currentPosition
     */

    public static void setCurrentPosition(SKCoordinate currentPosition) {
        OneBoxManager.currentPosition = currentPosition;
    }

    /**
     * Gets the current position sent with reportNewGPSPosition
     * @return coordinate(lat,long) for current position
     */
    public static double[] getCurrentPosition() {

        SKCoordinate pos = currentPosition;
        if (pos != null)
            return new double[]{pos.getLatitude(), pos.getLongitude()};
        return new double[]{46.8, 23.6};
    }

}
