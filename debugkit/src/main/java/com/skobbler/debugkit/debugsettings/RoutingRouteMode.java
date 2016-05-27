package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.routing.SKRouteSettings;

/**
 * Created by mirceab on 03.07.2015.
 */
public class RoutingRouteMode extends EnumBasedDebugSettings {
    @Override
    Class defineEnumClass() {
        return SKRouteSettings.SKRouteMode.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_route_mode_option;
    }
}
