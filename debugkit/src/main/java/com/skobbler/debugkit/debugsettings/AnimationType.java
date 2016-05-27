package com.skobbler.debugkit.debugsettings;

import android.util.Pair;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKAnimationSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 04.06.2015.
 */
public class AnimationType extends EnumBasedDebugSettings {

    @Override
    Class defineEnumClass() {
        return SKAnimationSettings.SKAnimationType.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.animation_type_debug_kit;
    }
}
