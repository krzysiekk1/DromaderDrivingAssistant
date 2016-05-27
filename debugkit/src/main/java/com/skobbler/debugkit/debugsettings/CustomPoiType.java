package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapCustomPOI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 18.06.2015.
 */
public class CustomPoiType extends EnumBasedDebugSettings {

    @Override
    Class defineEnumClass() {
        return SKMapCustomPOI.SKPoiType.class;
    }


    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.custom_poi_type_debug_kit;
    }

}
