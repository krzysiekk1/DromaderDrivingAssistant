package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKMaps;

/**
 * Created by mirceab on 19.06.2015.
 */
public class InternationalizationPrimaryOptionTwo extends EnumBasedDebugSettings {
    @Override
    Class defineEnumClass() {
        return SKMaps.SKLanguage.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.internationalization_primary_option_two_debug_kit;
    }
}
