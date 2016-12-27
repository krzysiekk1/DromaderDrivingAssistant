package com.skobbler.sdkdemo.navigationui.autonight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.skobbler.sdkdemo.navigationui.SKToolsLogicManager;

/**
 * Defines a BroadcastReceiver that listens for the alarm manager that sends the
 * broadcast at sunrise/sunset hours in order to change the map styles (day/night)
 */
public class SKToolsChangeMapStyleAutoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SKToolsLogicManager.getInstance().isNavigationStopped()) {
            SKToolsLogicManager.getInstance().computeMapStyle(SKToolsDateUtils.isDaytime());
        }
    }
}
