package com.skobbler.sdkdemo.navigationui.autonight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;

/**
 * Defines a BroadcastReceiver that listens for the alarm manager that sends the
 * broadcast hourly in order to recalculate the sunrise / sunset hours
 */
public class SKToolsCalculateSunriseSunsetTimeAutoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SKToolsLogicManager.lastUserPosition != null && !SKToolsLogicManager.getInstance().isNavigationStopped()) {
            SKToolsSunriseSunsetCalculator.calculateSunriseSunsetHours(SKToolsLogicManager.lastUserPosition.getCoordinate(),
                    SKToolsSunriseSunsetCalculator.OFFICIAL);

            SKToolsAutoNightManager.getInstance().setAlarmForDayNightModeWithSunriseSunset(SKToolsLogicManager
                    .getInstance().getCurrentActivity());
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotificationAfterKitKat(context, false);
        }

    }
}

