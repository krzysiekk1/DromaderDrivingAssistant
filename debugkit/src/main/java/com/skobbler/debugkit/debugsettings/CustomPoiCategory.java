package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCategories;

/**
 * Created by mirceab on 18.06.2015.
 */
public class CustomPoiCategory extends EnumBasedDebugSettings {
    @Override
    Class defineEnumClass() {
        return SKCategories.SKPOICategory.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.custom_poi_category_debug_kit;
    }
}
