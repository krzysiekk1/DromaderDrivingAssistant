package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.routing.SKRouteSettings;

/**
 * Created by mirceab on 03.07.2015.
 */
public class RoutingRouteConnectionMode extends EnumBasedDebugSettings {
    @Override
    Class defineEnumClass() {
        return SKRouteSettings.SKRouteConnectionMode.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_route_connection_mode_option;
    }
}
