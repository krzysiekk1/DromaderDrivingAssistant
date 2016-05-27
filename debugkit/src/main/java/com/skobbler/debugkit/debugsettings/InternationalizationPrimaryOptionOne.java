package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapInternationalizationSettings;

/**
 * Created by mirceab on 19.06.2015.
 */
public class InternationalizationPrimaryOptionOne extends EnumBasedDebugSettings {
    @Override
    Class defineEnumClass() {
        return SKMapInternationalizationSettings.SKMapInternationalizationOption.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.internationalization_primary_option_one_debug_kit;
    }
}
