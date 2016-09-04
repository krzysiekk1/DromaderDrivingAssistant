package com.skobbler.ngx.sdktools.onebox.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;

import java.text.DecimalFormat;


public class SKToolsUtils {

    public static final byte HAS_INTERNET = 1;

    public static final byte ONLINE_MODE = 2;

    public static final byte HAS_INTERNET_ONLINE_MODE = 3;

    /**
     * Factor to convert rad into degree.
     */
    public static final double RAD2DEGFACTOR = 180.0 / Math.PI;

    /**
     * Factor to convert degree into rad.
     */
    public static final double DEG2RADFACTOR = Math.PI / 180.0;

    /**
     * Factor <i>pi</i> / 2.
     */
    public static final double KHalfPi = (Math.PI / 2d);

    /**
     * the number of meters in a km
     */
    public static final int METERSINKM = 1000;

    /**
     * the number of meters in a mile
     */
    public static final double METERSINMILE = 1609.34;

    /**
     * the number of feet in a mile
     */
    public static final int FEETINMILE = 5280;

    /**
     * the number of km/h in 1 m/s
     */
    public static final double SPEED_IN_KILOMETRES = 3.6;

    /**
     * number of mi/h in 1 m/s
     */
    public static final double SPEED_IN_MILES = 2.2369;

    /**
     * the number of yards in a mile
     */
    public static final int YARDSINMILE = 1760;

    /**
     * converter from meters to yards
     */
    public static final double METERSTOYARDS = 1.0936133;

    /**
     * the number of feet in a yard
     */
    public static final int FEETINYARD = 3;

    /**
     * the limit of feet where the distance should be converted into miles
     */
    public static final int LIMIT_TO_MILES = 1500;

    /**
     * The pattern used to format the distance shown in post navigation screen
     */
    public static final String ONE_DECIMAL_DISTANCE_FORMATTER_PATTERN = "0.0";

    /**
     * converter from meters to feet
     */
    public static final double METERSTOFEET = 3.2808399;

    /**
     * The radius of the earth as arithmetic mean of small and large semi-axis.
     * @todo this radius is inaccurate! Oblateness not considered. WGS84 should
     * be 6371000.8
     */
    private static final double KEarthRadius = 6367444;

    /**
     * Radius of the large equatorial axis a refering to the WGS84 ellipsoid
     * (1979).
     */
    private static final double KEquatorialRadius = 6378137.0d;

    /**
     * Radius of the small polar axis b refering to the WGS84 ellipsoid (1979).
     */
    private static final double KPolarRadius = 6356752.3142d;

    /**
     * Convenience method for {@link #distanceBetween(double, double, double, double)}
     * @return distance on surface in meter
     */
    public static double distanceBetween(final SKCoordinate point_A, final SKCoordinate point_B) {
        return distanceBetween(point_A.getLongitude(), point_A.getLatitude(), point_B.getLongitude(), point_B.getLatitude());
    }

    /**
     * Calculates the distance between the points (point_A_long, point_A_lat)
     * and (point_B_long, point_B_lat). We assume that the earth is an ellipsoid
     * with two different semi-axis. The curvature of the earth is not
     * considered.
     * <p/>
     * This method should be used for distance calculation if the points are
     * closer than 5 km. The calculation needs less power than the SphereLength
     * method and is more accurate for small distances.
     * <p/>
     * On large distances, the missing correction of earth curvature will
     * influence the result.
     * @param point_A_long the longitude of the first point in decimal degree
     * @param point_A_lat latitude of the first point in decimal degree
     * @param point_B_long longitude of the second point in decimal degree
     * @param point_B_lat latitude of the seconds point in decimal degree
     * @return distance on surface in meter
     */
    public static double distanceBetween(final double point_A_long, final double point_A_lat,
                                         final double point_B_long, final double point_B_lat) {
        // calculates angle between latitude
        final double deltaLat = (point_B_lat - point_A_lat) * DEG2RADFACTOR;
        // calculates angle between longitude
        final double deltaLon = (point_B_long - point_A_long) * DEG2RADFACTOR;
        // calculates the earth readius at the specific latitude
        final double currentRadius = KEquatorialRadius * Math.cos(point_A_lat * DEG2RADFACTOR);
        // multiplies the laitude by the smaller polar radius
        final double meter_Y = KPolarRadius * deltaLat;
        // multiplies the longitude by the current earth radius.
        final double meter_X = currentRadius * deltaLon;
        // calculates the distance between the two points assuming that the
        // curvature
        // is equal in X and Y using pythagos' theorem.
        return Math.sqrt(meter_X * meter_X + meter_Y * meter_Y);
    }

    /**
     * converts a distance given in meters to the according distance in yards
     * @param distanceInMeters
     * @return
     */
    public static double distanceInYards(double distanceInMeters) {
        if (distanceInMeters != -1) {
            return distanceInMeters * METERSTOYARDS;
        } else {
            return distanceInMeters;
        }
    }

    /**
     * converts a distance given in meters to the according distance in feet
     * @param distanceInMeters
     * @return
     */
    public static double distanceInFeet(double distanceInMeters) {
        if (distanceInMeters != -1) {
            return distanceInMeters * METERSTOYARDS * FEETINYARD;
        } else {
            return distanceInMeters;
        }
    }

    /**
     * converts the distance given in feet/yards/miles/km to the according distance in meters
     * @param distance
     * @param initialUnit: 0 - feet
     * 1 - yards
     * 2 - mile
     * 3 - km
     * @return distance in meters
     */
    public static double distanceInMeters(double distance, int initialUnit) {
        if (distance != -1) {
            switch (initialUnit) {
                case 0:
                    return distance /= METERSTOFEET;
                case 1:
                    return distance /= METERSTOYARDS;
                case 2:
                    return distance *= METERSINMILE;
                case 3:
                    return distance *= METERSINKM;
            }
        }
        return distance;
    }


    /**
     * Formats the distance using the input pattern
     * @param distanceToFormat the distance to be formatted
     * @param formatPattern the pattern used to format
     * @return the String formatted distance
     */
    public static String formatDistance(double distanceToFormat, String formatPattern) {
        if (formatPattern != null) {
            String formattedDistance = new DecimalFormat(formatPattern).format(distanceToFormat);
            // the values we return have to be contain '.', not ',' (ex: 8.5 not
            // 8,5), because they have to be parsed further on
            return formattedDistance.replace(',', '.');
        } else {
            return String.valueOf(Math.round(distanceToFormat * 10) / 10).replace(',', '.');
        }
    }

    /**
     * Method used to convert speed from m/s into km/h or mi/h (according to the
     * distance unit set from Setting option)
     * @param initialSpeed - the speed in m/s
     * @param distanceUnitType
     * @return an int value for speed in km/h or mi/h
     */
    public static int getSpeedByUnit(double initialSpeed, SKMaps.SKDistanceUnitType distanceUnitType) {
        double tempSpeed = initialSpeed;
        if (distanceUnitType == SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS) {
            tempSpeed *= SPEED_IN_KILOMETRES;
        } else {
            tempSpeed *= SPEED_IN_MILES;
        }
        return (int) Math.round(tempSpeed);
    }
    /**
     * Converts (to imperial units if necessary) and formats as string a
     * distance value given in meters.
     * @param distanceValue distance value in meters
     * distance unit labels
     * @return
     */
    public static String convertAndformatDistance(double distanceValue, SKMaps.SKDistanceUnitType distanceUnitType) {


        String distanceValueText = null;

        if (distanceUnitType == SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS) {
            if (distanceValue >= SKToolsUtils.METERSINKM) {
                distanceValue /= SKToolsUtils.METERSINKM;
                if (distanceValue >= 10) {
                    // FMA-2577:
                    // if distance is >= 10 km => display distance without any
                    // decimals
                    distanceValueText = (Math.round(distanceValue) + " " + "km");
                } else {
                    // distance displayed in kilometers
                    distanceValueText = (((float) Math.round(distanceValue * 10) / 10)) + " " + "km";
                }
            } else {
                // distance displayed in meters
                distanceValueText = ((int) distanceValue) + " " + "m";
            }
        }

        if ((distanceValueText != null) && distanceValueText.startsWith("0 ")) {
            return "";
        }
        return distanceValueText;

    }


    /**
     * Tells if internet is currently available on the device
     * @param currentContext
     * @return
     */
    public static boolean isInternetAvailable(Context currentContext) {
        ConnectivityManager conectivityManager =
                (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


}
