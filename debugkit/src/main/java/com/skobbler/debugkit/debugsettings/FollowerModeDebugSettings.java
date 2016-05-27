package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapSettings;

/**
 * Created by Tudor on 6/4/2015.
 */
public class FollowerModeDebugSettings extends EnumBasedDebugSettings {

    @Override
    Class defineEnumClass() {
        return SKMapSettings.SKMapFollowerMode.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_follower_mode;
    }
}
